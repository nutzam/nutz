package org.nutz.dao.util;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.nutz.dao.Chain;
import org.nutz.dao.Condition;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;
import org.nutz.trans.Molecule;
import org.nutz.trans.Trans;

/**
 * Dao 的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author cqyunqin
 */
public abstract class Daos {
    
    private static final Log log = Logs.get();

    public static void safeClose(Statement stat, ResultSet rs) {
        safeClose(rs);
        safeClose(stat);
    }

    public static void safeClose(Statement stat) {
        if (null != stat)
            try {
                stat.close();
            }
            catch (Throwable e) {}
    }

    public static void safeClose(ResultSet rs) {
        if (null != rs)
            try {
                rs.close();
            }
            catch (Throwable e) {}
    }

    public static int getColumnIndex(ResultSetMetaData meta, String colName) throws SQLException {
        if (meta == null)
            return 0;
        int columnCount = meta.getColumnCount();
        for (int i = 1; i <= columnCount; i++)
            if (meta.getColumnName(i).equalsIgnoreCase(colName))
                return i;
        // TODO 尝试一下meta.getColumnLabel?
        log.infof("Can not find @Column(%s) in database", colName);
        throw Lang.makeThrow(SQLException.class, "Can not find @Column(%s)", colName);
    }

    public static boolean isIntLikeColumn(ResultSetMetaData meta, int index) throws SQLException {
        switch (meta.getColumnType(index)) {
        case Types.BIGINT:
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
        case Types.NUMERIC:
            return true;
        }
        return false;
    }

    public static Pager updatePagerCount(Pager pager, Dao dao, Class<?> entityType, Condition cnd) {
        if (null != pager) {
            pager.setRecordCount(dao.count(entityType, cnd));
        }
        return pager;
    }

    public static Pager updatePagerCount(Pager pager, Dao dao, String tableName, Condition cnd) {
        if (null != pager) {
            pager.setRecordCount(dao.count(tableName, cnd));
        }
        return pager;
    }

    public static <T> List<T> queryList(Dao dao, Class<T> klass, String sql_str) {
        Sql sql = Sqls.create(sql_str)
                        .setCallback(Sqls.callback.entities())
                        .setEntity(dao.getEntity(klass));
        dao.execute(sql);
        return sql.getList(klass);
    }

    public static Object query(Dao dao, String sql_str, SqlCallback callback) {
        Sql sql = Sqls.create(sql_str).setCallback(callback);
        dao.execute(sql);
        return sql.getResult();
    }

    public static <T> List<T> queryWithLinks(    final Dao dao,
                                                final Class<T> classOfT,
                                                final Condition cnd,
                                                final Pager pager,
                                                final String regex) {
        Molecule<List<T>> molecule = new Molecule<List<T>>() {
            public void run() {
                List<T> list = dao.query(classOfT, cnd, pager);
                for (T t : list)
                    dao.fetchLinks(t, regex);
                setObj(list);
            }
        };
        return Trans.exec(molecule);
    }

    /*根据Pojo生成数据字典,zdoc格式*/
    public static StringBuilder dataDict(DataSource ds, String...packages) {
        StringBuilder sb = new StringBuilder();
        List<Class<?>> ks = new ArrayList<Class<?>>();
        for (String packageName : packages) {
            ks.addAll(Scans.me().scanPackage(packageName));
        }
        Iterator<Class<?>> it = ks.iterator();
        while (it.hasNext()) {
            Class<?> klass = it.next();
            if (klass.getAnnotation(Table.class) == null)
                it.remove();
        }
        //log.infof("Found %d table class", ks.size());
        
        JdbcExpert exp = Jdbcs.getExpert(ds);
        NutDao dao = new NutDao(ds);
        
        Method evalFieldType;
        try {
            evalFieldType = exp.getClass().getDeclaredMethod("evalFieldType", MappingField.class);
        } catch (Throwable e) {
            throw Lang.wrapThrow(e);
        }
        evalFieldType.setAccessible(true);
        Entity<?> entity = null;
        String line = "-------------------------------------------------------------------\n";
        sb.append("#title:数据字典\n");
        sb.append("#author:wendal\n");
        sb.append("#index:0,1\n").append(line);
        for (Class<?> klass : ks) {
            sb.append(line);
            entity = dao.getEntity(klass);
            sb.append("表名 ").append(entity.getTableName()).append("\n\n");
                if (!Strings.isBlank(entity.getTableComment()))
                    sb.append("表注释: ").append(entity.getTableComment());
                sb.append("\t").append("Java类名 ").append(klass.getName()).append("\n\n");
                sb.append("\t||序号||列名||数据类型||主键||非空||默认值||java属性名||java类型||注释||\n");
                int index = 1;
                for (MappingField field : entity.getMappingFields()) {
                    String dataType;
                    try {
                        dataType = (String) evalFieldType.invoke(exp, field);
                    } catch (Throwable e) {
                        throw Lang.wrapThrow(e); //不可能发生的
                    }
                    sb.append("\t||").append(index++).append("||")
                        .append(field.getColumnName()).append("||")
                        .append(dataType).append("||")
                        .append(field.isPk()).append("||")
                        .append(field.isNotNull()).append("||")
                        .append(field.getDefaultValue(null) == null ? " " : field.getDefaultValue(null)).append("||")
                        .append(field.getName()).append("||")
                        .append(field.getTypeClass().getName()).append("||")
                        .append(field.getColumnComment() == null ? " " : field.getColumnComment()).append("||\n");
                }
        }
        return sb;
    }
    
    /**
     * 查询sql并把结果放入传入的class组成的List中
     */
    public static <T> List<T> query(Dao dao, Class<T> classOfT, String sql, Condition cnd, Pager pager) {
        Sql sql2 = Sqls.queryEntity(sql);
        sql2.setEntity(dao.getEntity(classOfT));
        sql2.setCondition(cnd);
        sql2.setPager(pager);
        dao.execute(sql2);
        return sql2.getList(classOfT);
    }
    
    /**
     * 查询某sql的结果条数
     */
    public static int queryCount(Dao dao, String sql) {
        Sql sql2 = Sqls.fetchLong("select count(1) FROM ("+sql+")");
        dao.execute(sql2);
        return sql2.getInt();
    }
    
    /**
     * 执行一个特殊的Chain(事实上普通Chain也能执行,但不建议使用)
     * @see org.nutz.dao.Chain#addSpecial(String, Object)
     */
    @SuppressWarnings({ "rawtypes" })
    public static int updateBySpecialChain(Dao dao, Entity en, String tableName, Chain chain, Condition cnd) {
        if (en != null)
            tableName = en.getTableName();
        if (tableName == null)
            throw Lang.makeThrow(DaoException.class, "tableName and en is NULL !!");
        final StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        Chain head = chain.head();
        final List<Object> values = new ArrayList<Object>();
        final List<ValueAdaptor> adaptors = new ArrayList<ValueAdaptor>();
        while (head != null) {
            MappingField mf = null;
            if (en != null)
                mf = en.getField(head.name());
            String colName = head.name();
            if (mf != null)
                colName = mf.getColumnName();
            sql.append(colName).append("=");
            if (head.special()) {
                if ("+1".equals(head.value()) || "-1".equals(head.value())) {
                    sql.append(colName);
                }
                sql.append(head.value());
            } else {
                sql.append("?");
                values.add(head.value());
                ValueAdaptor adaptor = Jdbcs.getAdaptorBy(head.value());
                if (mf != null && mf.getAdaptor() != null)
                    adaptor = mf.getAdaptor();
                adaptors.add(adaptor);
            }
            sql.append(" ");
            head = head.next();
            if (head != null)
                sql.append(", ");
        }
        if (cnd != null)
            sql.append(" ").append(cnd.toSql(en));
        if (log.isDebugEnabled())
            log.debug(sql);
        final int[] ints = new int[1];
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                PreparedStatement ps = conn.prepareStatement(sql.toString());
                try {
                    for (int i = 0; i < values.size(); i++)
                        adaptors.get(i).set(ps, values.get(i), i + 1);
                    ints[0] = ps.executeUpdate();
                } finally {
                    Daos.safeClose(ps);
                }
            }
        });
        return ints[0];
    }
}
