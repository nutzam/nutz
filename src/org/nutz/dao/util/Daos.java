package org.nutz.dao.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.nutz.dao.Chain;
import org.nutz.dao.Condition;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityIndex;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.sql.SqlFormat;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Callback2;
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
 * @author rekoe(koukou890@qq.com)
 */
public abstract class Daos {

    private static final Log log = Logs.get();

    /**
     * 安全关闭Statement和ResultSet
     *
     * @param stat
     *            Statement实例,可以为null
     * @param rs
     *            ResultSet实例,可以为null
     */
    public static void safeClose(Statement stat, ResultSet rs) {
        safeClose(rs);
        safeClose(stat);
    }

    /**
     * 安全关闭Statement
     *
     * @param stat
     *            Statement实例,可以为null
     */
    public static void safeClose(Statement stat) {
        if (null != stat)
            try {
                stat.close();
            }
            catch (Throwable e) {}
    }

    /**
     * 安全关闭=ResultSet
     *
     * @param rs
     *            ResultSet实例,可以为null
     */
    public static void safeClose(ResultSet rs) {
        if (null != rs)
            try {
                rs.close();
            }
            catch (Throwable e) {}
    }

    /**
     * 获取colName所在的行数
     *
     * @param meta
     *            从连接中取出的ResultSetMetaData
     * @param colName
     *            字段名
     * @return 所在的索引,如果不存在就抛出异常
     * @throws SQLException
     *             指定的colName找不到
     */
    public static int getColumnIndex(ResultSetMetaData meta, String colName) throws SQLException {
        if (meta == null)
            return 0;
        int columnCount = meta.getColumnCount();
        for (int i = 1; i <= columnCount; i++)
            if (meta.getColumnName(i).equalsIgnoreCase(colName))
                return i;
        // TODO 尝试一下meta.getColumnLabel?
        log.debugf("Can not find @Column(%s) in table/view (%s)", colName, meta.getTableName(1));
        throw Lang.makeThrow(SQLException.class, "Can not find @Column(%s)", colName);
    }

    /**
     * 是不是数值字段
     *
     * @param meta
     *            从连接中取出的ResultSetMetaData
     * @param index
     *            字段索引
     * @return 如果是就返回true
     * @throws SQLException
     *             指定的索引不存在
     */
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

    /**
     * 填充记录总数
     *
     * @param pager
     *            分页对象,如果为null就不进行任何操作
     * @param dao
     *            Dao实例
     * @param entityType
     *            实体类,可以通过dao.getEntity获取
     * @param cnd
     *            查询条件
     * @return 传入的Pager参数
     */
    public static Pager updatePagerCount(Pager pager, Dao dao, Class<?> entityType, Condition cnd) {
        if (null != pager) {
            pager.setRecordCount(dao.count(entityType, cnd));
        }
        return pager;
    }

    /**
     * 填充记录总数
     *
     * @param pager
     *            分页对象,如果为null就不进行任何操作
     * @param dao
     *            Dao实例
     * @param tableName
     *            表名
     * @param cnd
     *            查询条件
     * @return 传入的Pager参数
     */
    public static Pager updatePagerCount(Pager pager, Dao dao, String tableName, Condition cnd) {
        if (null != pager) {
            pager.setRecordCount(dao.count(tableName, cnd));
        }
        return pager;
    }

    /**
     * 根据sql查询特定的记录,并转化为指定的类对象
     *
     * @param dao
     *            Dao实例
     * @param klass
     *            Pojo类
     * @param sql_str
     *            sql语句
     * @return 查询结果
     */
    public static <T> List<T> queryList(Dao dao, Class<T> klass, String sql_str) {
        Sql sql = Sqls.create(sql_str)
                      .setCallback(Sqls.callback.entities())
                      .setEntity(dao.getEntity(klass));
        dao.execute(sql);
        return sql.getList(klass);
    }

    /**
     * 执行sql和callback
     *
     * @param dao
     *            Dao实例
     * @param sql_str
     *            sql语句
     * @param callback
     *            sql回调
     * @return 回调的返回值
     */
    public static Object query(Dao dao, String sql_str, SqlCallback callback) {
        Sql sql = Sqls.create(sql_str).setCallback(callback);
        dao.execute(sql);
        return sql.getResult();
    }

    /**
     * 在同一个事务内查询对象及关联对象
     *
     * @param dao
     *            Dao实例
     * @param classOfT
     *            指定的Pojo类
     * @param cnd
     *            查询条件
     * @param pager
     *            分页语句
     * @param regex
     *            需要查出的关联对象, 可以参阅dao.fetchLinks
     * @return 查询结果
     */
    public static <T> List<T> queryWithLinks(final Dao dao,
                                             final Class<T> classOfT,
                                             final Condition cnd,
                                             final Pager pager,
                                             final String regex) {
        Molecule<List<T>> molecule = new Molecule<List<T>>() {
            public void run() {
                List<T> list = dao.query(classOfT, cnd, pager);
                dao.fetchLinks(list, regex);
                setObj(list);
            }
        };
        return Trans.exec(molecule);
    }
    
    public static StringBuilder dataDict(DataSource ds, String... packages) {
        return dataDict(new NutDao(ds), packages);
    }

    /** 根据Pojo生成数据字典,zdoc格式 */
    public static StringBuilder dataDict(Dao dao, String... packages) {
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
        // log.infof("Found %d table class", ks.size());

        JdbcExpert exp = dao.getJdbcExpert();
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
                String dataType = exp.evalFieldType(field);
                sb.append("\t||")
                  .append(index++)
                  .append("||")
                  .append(field.getColumnName())
                  .append("||")
                  .append(dataType)
                  .append("||")
                  .append(field.isPk())
                  .append("||")
                  .append(field.isNotNull())
                  .append("||")
                  .append(field.getDefaultValue(null) == null ? " " : field.getDefaultValue(null))
                  .append("||")
                  .append(field.getName())
                  .append("||")
                  .append(field.getTypeClass().getName())
                  .append("||")
                  .append(field.getColumnComment() == null ? " " : field.getColumnComment())
                  .append("||\n");
            }
        }
        return sb;
    }

    /**
     * 查询sql并把结果放入传入的class组成的List中
     */
    public static <T> List<T> query(Dao dao,
                                    Class<T> classOfT,
                                    String sql,
                                    Condition cnd,
                                    Pager pager) {
        Sql sql2 = Sqls.queryEntity(sql);
        sql2.setEntity(dao.getEntity(classOfT));
        sql2.setCondition(cnd);
        sql2.setPager(pager);
        dao.execute(sql2);
        return sql2.getList(classOfT);
    }

    /**
     * 查询某sql的结果条数. 请使用Sql接口的版本
     */
    @Deprecated
    public static long queryCount(Dao dao, String sql) {
        String tmpTable = "as _nutz_tmp";
        if (dao.meta().isDB2())
            tmpTable = "as nutz_tmp_" + R.UU32();
        else if (dao.meta().isOracle())
            tmpTable = "";
        else
            tmpTable += "_" + R.UU32();
        Sql sql2 = Sqls.fetchLong("select count(1) from (" + sql + ")" + tmpTable);
        dao.execute(sql2);
        return sql2.getLong();
    }
    
    /**
     * 查询某sql的结果条数
     * @param dao 用于执行该count方法的dao实例
     * @param sql 原本的Sql对象,将复制其sql语句,变量和参数表.
     */
    public static long queryCount(Dao dao, Sql sql) {
        String tmpTable = "as _nutz_tmp";
        if (dao.meta().isDB2())
            tmpTable = "as nutz_tmp_" + R.UU32();
        else if (dao.meta().isOracle())
            tmpTable = "";
        else
            tmpTable += "_" + R.UU32();
        Sql sql2 = Sqls.fetchLong("select count(1) from (" + sql.getSourceSql() + ")" + tmpTable);
        for (String key : sql.params().keys()) {
            sql2.setParam(key, sql.params().get(key));
        }
        for (String key : sql.vars().keys()) {
            sql2.setVar(key, sql.vars().get(key));
        }
        return dao.execute(sql2).getLong();
    }

    /**
     * 执行一个特殊的Chain(事实上普通Chain也能执行,但不建议使用)
     *
     * @see org.nutz.dao.Chain#addSpecial(String, Object)
     */
    @SuppressWarnings({"rawtypes"})
    public static void insertBySpecialChain(Dao dao, Entity en, String tableName, Chain chain) {
        if (en != null)
            tableName = en.getTableName();
        if (tableName == null)
            throw Lang.makeThrow(DaoException.class, "tableName and en is NULL !!");
        final StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder _value_places = new StringBuilder(" VALUES(");
        final List<Object> values = new ArrayList<Object>();
        final List<ValueAdaptor> adaptors = new ArrayList<ValueAdaptor>();
        Chain head = chain.head();
        while (head != null) {
            String colName = head.name();
            MappingField mf = null;
            if (en != null) {
                mf = en.getField(colName);
                if (mf != null)
                    colName = mf.getColumnNameInSql();
            }
            sql.append(colName);

            if (head.special()) {
                _value_places.append(head.value());
            } else {
                if (en != null)
                    mf = en.getField(head.name());
                _value_places.append("?");
                values.add(head.value());
                ValueAdaptor adaptor = Jdbcs.getAdaptorBy(head.value());
                if (mf != null && mf.getAdaptor() != null)
                    adaptor = mf.getAdaptor();
                adaptors.add(adaptor);
            }

            head = head.next();
            if (head != null) {
                sql.append(", ");
                _value_places.append(", ");
            }
        }
        sql.append(")");
        _value_places.append(")");
        sql.append(_value_places);
        if (log.isDebugEnabled())
            log.debug(sql);
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                PreparedStatement ps = conn.prepareStatement(sql.toString());
                try {
                    for (int i = 0; i < values.size(); i++)
                        adaptors.get(i).set(ps, values.get(i), i + 1);
                    ps.execute();
                }
                finally {
                    Daos.safeClose(ps);
                }
            }
        });
    }

    /**
     * 为特定package下带@Table注解的类调用dao.create(XXX.class, force),
     * 批量建表,优先建立带@ManyMany的表
     *
     * @param dao
     *            Dao实例
     * @param packageName
     *            package名称,自动包含子类
     * @param force
     *            如果表存在,是否先删后建
     */
    public static void createTablesInPackage(final Dao dao, String packageName, boolean force) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        for(Class<?> klass: Scans.me().scanPackage(packageName)) {
            if (klass.getAnnotation(Table.class) != null)
                list.add(klass);
        };
        Collections.sort(list, new Comparator<Class<?>>() {
            public int compare(Class<?> prev, Class<?> next) {
                int links_prev = dao.getEntity(prev).getLinkFields(null).size();
                int links_next = dao.getEntity(next).getLinkFields(null).size();
                if (links_prev == links_next)
                    return 0;
                return links_prev > links_next ? 1 : -1;
            }
            
        });
        for (Class<?> klass : list)
            dao.create(klass, force);
    }

    /**
     * 为特定package下带@Table注解的类调用dao.create(XXX.class, force), 批量建表
     *
     * @param dao
     *            Dao实例
     * @param oneClzInPackage
     *            使用package中某一个class文件, 可以防止写错pkgName
     * @param force
     *            如果表存在,是否先删后建
     */
    public static void createTablesInPackage(Dao dao, Class<?> oneClzInPackage, boolean force) {
        createTablesInPackage(dao, oneClzInPackage.getPackage().getName(), force);
    }

    private static Class<?>[] iz = new Class<?>[]{Dao.class};

    /**
     * 创建一个带FieldFilter的Dao代理实例. 注意,为避免出错,生成的Dao对象不应该传递到其他方法去.
     *
     * @param dao
     *            原始的Dao实例
     * @param filter
     *            字段过滤器
     * @return 带FieldFilter的Dao代理实例
     */
    public static Dao ext(Dao dao, FieldFilter filter) {
        return ext(dao, filter, null);
    }

    /**
     * 创建一个带TableName的Dao代理实例. 注意,为避免出错,生成的Dao对象不应该传递到其他方法去.
     *
     * @param dao
     *            原始的Dao实例
     * @param tableName
     *            动态表名上下文
     * @return 带TableName的Dao代理实例
     */
    public static Dao ext(Dao dao, Object tableName) {
        return ext(dao, null, tableName);
    }

    /**
     * 同时进行字段过滤和动态表名封装
     *
     * @param dao
     *            Dao实例
     * @param filter
     *            字段过滤
     * @param tableName
     *            动态表名参数
     * @return 封装好的Dao实例
     */
    public static Dao ext(Dao dao, FieldFilter filter, Object tableName) {
        if (tableName == null && filter == null)
            return dao;
        ExtDaoInvocationHandler handler = new ExtDaoInvocationHandler(dao, filter, tableName);
        return (Dao) Proxy.newProxyInstance(dao.getClass().getClassLoader(), iz, handler);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean filterFields(Object obj,
                                       FieldMatcher matcher,
                                       Dao dao,
                                       Callback2<MappingField, Object> callback) {
        if (obj == null)
            return false;
        obj = Lang.first(obj);
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == Class.class) {
            throw Lang.impossible();
        }
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            throw Lang.impossible();
        }
        Entity en = dao.getEntity(obj.getClass());
        if (en == null) {
            throw Lang.impossible();
        }

        List<MappingField> mfs = new ArrayList<MappingField>(en.getMappingFields());
        if (matcher != null) {
            Iterator<MappingField> it = mfs.iterator();
            while (it.hasNext()) {
                MappingField mf = it.next();
                if (!matcher.match(mf.getName()))
                    it.remove();
            }
        }
        boolean flag = false;
        for (MappingField mf : mfs) {
            if (matcher == null) {
                Object val = mf.getValue(obj);
                callback.invoke(mf, val);
                flag = true;
                continue;
            }
            if (matcher.isIgnoreId() && mf.isId())
                continue;
            if (matcher.isIgnoreName() && mf.isName())
                continue;
            if (matcher.isIgnorePk() && mf.isCompositePk())
                continue;
            Object val = mf.getValue(obj);
            if (val == null) {
                if (matcher.isIgnoreNull())
                    continue;
            } else {
                if (matcher.isIgnoreZero()
                    && val instanceof Number
                    && ((Number) val).doubleValue() == 0.0) {
                    continue;
                }
                if (matcher.isIgnoreDate() && val instanceof Date) {
                    continue;
                }
                if (matcher.isIgnoreBlankStr()
                    && val instanceof CharSequence
                    && Strings.isBlank((CharSequence) val)) {
                    continue;
                }
            }
            callback.invoke(mf, val);
            flag = true;
        }
        return flag;
    }

    /**
     * 为数据表自动增减字段
     *
     * @param dao
     *            Dao实例
     * @param klass
     *            映射Pojo
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     * @param checkIndex
     *            是否检查索引
     */
    public static void migration(Dao dao,
                                 final Class<?> klass,
                                 final boolean add,
                                 final boolean del,
                                 boolean checkIndex) {
        migration(dao, klass, add, del, checkIndex, null);
    }

    /**
     * 为数据表自动增减字段
     *
     * @param dao
     *            Dao实例
     * @param klass
     *            映射Pojo
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     */
    public static void migration(Dao dao,
                                 final Class<?> klass,
                                 final boolean add,
                                 final boolean del) {
        migration(dao, klass, add, del, false, null);
    }

    /**
     * 为数据表自动增减字段
     *
     * @param dao
     *            Dao实例
     * @param klass
     *            映射Pojo
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     * @param tableName
     *            动态表名上下文
     */
    public static void migration(Dao dao,
                                 final Class<?> klass,
                                 final boolean add,
                                 final boolean del,
                                 Object tableName) {
        migration(dao, klass, add, del, false, tableName);
    }

    /**
     * 为数据表自动增减字段
     *
     * @param dao
     *            Dao实例
     * @param klass
     *            映射Pojo
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     * @param checkIndex
     *            是否检查索引
     * @param tableName
     *            动态表名上下文
     */
    public static void migration(Dao dao,
                                 final Class<?> klass,
                                 final boolean add,
                                 final boolean del,
                                 final boolean checkIndex,
                                 final Object tableName) {
        final JdbcExpert expert = dao.getJdbcExpert();
        if (tableName != null && Strings.isNotBlank(tableName.toString())) {
            dao = ext(dao, tableName);
        }
        final Entity<?> en = dao.getEntity(klass);
        if (!dao.exists(klass))
            return;
        final List<Sql> sqls = new ArrayList<Sql>();
        final Set<String> _indexs = new HashSet<String>();
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                expert.setupEntityField(conn, en);
                Statement stat = null;
                ResultSet rs = null;
                ResultSetMetaData meta = null;
                try {
                    // 获取数据库元信息
                    stat = conn.createStatement();
                    rs = stat.executeQuery("select * from " + en.getTableName() + " where 1 != 1");
                    meta = rs.getMetaData();

                    Set<String> columnNames = new HashSet<String>();
                    int columnCount = meta.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(meta.getColumnName(i).toLowerCase());
                    }
                    for (MappingField mf : en.getMappingFields()) {
                        if (mf.isReadonly())
                            continue;
                        String colName = mf.getColumnName();
                        if (columnNames.contains(colName.toLowerCase())) {
                            columnNames.remove(colName.toLowerCase());
                            continue;
                        }
                        if (add) {
                            log.infof("add column[%s] to table[%s]",
                                      mf.getColumnName(),
                                      en.getTableName());
                            sqls.add(expert.createAddColumnSql(en, mf));
                        }
                    }
                    if (del) {
                        for (String colName : columnNames) {
                            log.infof("del column[%s] from table[%s]", colName, en.getTableName());
                            Sql sql = Sqls.create("ALTER table $table DROP column $name");
                            sql.vars().set("table", en.getTableName());
                            sql.vars().set("name", colName);
                            sqls.add(sql);
                        }
                    }
                    // show index from mytable;
                    if (checkIndex)
                        _indexs.addAll(expert.getIndexNames(en, conn));
                }
                catch (SQLException e) {
                    if (log.isDebugEnabled())
                        log.debugf("migration Table '%s' fail!", en.getTableName(), e);
                }
                // Close ResultSet and Statement
                finally {
                    Daos.safeClose(stat, rs);
                }
            }
        });
        // 创建索引
        UpdateIndexSql indexSqls = createIndexs(dao, en, _indexs, tableName);
        if (checkIndex) {
            // 因为已删除的字段的索引是没办法删除的 所以要先处理索引 再处理字段
            Sql[] delIndexSqls = indexSqls.getSqlsDel();
            if (!Lang.isEmptyArray(delIndexSqls)) {
                dao.execute(delIndexSqls);
            }
        }
        for (Sql sql : sqls) {
            dao.execute(sql);
        }
        if (checkIndex) {
            Sql[] addIndexSqls = indexSqls.getSqlsAdd();
            if (!Lang.isEmptyArray(addIndexSqls)) {
                dao.execute(addIndexSqls);
            }
        }
        // 创建关联表
        dao.getJdbcExpert().createRelation(dao, en);
    }

    static class UpdateIndexSql {
        private Sql[] sqlsAdd;
        private Sql[] sqlsDel;

        public Sql[] getSqlsAdd() {
            return sqlsAdd;
        }

        public void setSqlsAdd(Sql[] sqlsAdd) {
            this.sqlsAdd = sqlsAdd;
        }

        public Sql[] getSqlsDel() {
            return sqlsDel;
        }

        public void setSqlsDel(Sql[] sqlsDel) {
            this.sqlsDel = sqlsDel;
        }

    }

    private static UpdateIndexSql createIndexs(Dao dao,
                                               Entity<?> en,
                                               Set<String> indexsHis,
                                               Object t) {
        UpdateIndexSql uis = new UpdateIndexSql();
        List<Sql> sqls = new ArrayList<Sql>();
        List<String> delIndexs = new ArrayList<String>();
        List<EntityIndex> indexs = en.getIndexes();
        for (EntityIndex index : indexs) {
            String indexName = index.getName(en);
            // 索引存在, 不要动
            if (indexsHis.contains(indexName)) {
                indexsHis.remove(indexName);
            }
            // 不存在,则新增
            else {
                sqls.add(dao.getJdbcExpert().createIndexSql(en, index));
            }
        }
        uis.setSqlsAdd(sqls.toArray(new Sql[sqls.size()]));
        // 剩余的,就是要删除的
        Iterator<String> iterator = indexsHis.iterator();
        List<Sql> delSqls = new ArrayList<Sql>();
        while (iterator.hasNext()) {
            String indexName = iterator.next();
            if (delIndexs.contains(indexName) || Lang.equals("PRIMARY", indexName)) {
                continue;
            }
            MappingField mf = en.getColumn(indexName);
            if (mf != null) {
                if (mf.isName())
                    continue;
            }
            if (dao.meta().isSqlServer()) {
                delSqls.add(Sqls.createf("DROP INDEX %s.%s",
                                         getTableName(dao, en, t),
                                         indexName));
            } else {
                delSqls.add(Sqls.createf("ALTER TABLE %s DROP INDEX %s",
                                         getTableName(dao, en, t),
                                         indexName));
            }
        }
        uis.setSqlsDel(delSqls.toArray(new Sql[delSqls.size()]));
        return uis;
    }

    /**
     * 为指定package及旗下package中带@Table注解的Pojo执行migration
     *
     * @param dao
     *            Dao实例
     * @param packageName
     *            指定的package名称
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     * @param checkIndex
     *            是否检查索引
     * @param nameTable
     *            动态表名上下文
     */
    public static void migration(Dao dao,
                                 String packageName,
                                 boolean add,
                                 boolean del,
                                 boolean checkIndex,
                                 Object nameTable) {
        for (Class<?> klass : Scans.me().scanPackage(packageName)) {
            if (klass.getAnnotation(Table.class) != null) {
                migration(dao, klass, add, del, checkIndex, nameTable);
            }
        }
    }

    /**
     * 为指定package及旗下package中带@Table注解的Pojo执行migration
     *
     * @param dao
     *            Dao实例
     * @param packageName
     *            指定的package名称
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     * @param nameTable
     *            动态表名上下文
     */
    public static void migration(Dao dao,
                                 String packageName,
                                 boolean add,
                                 boolean del,
                                 Object nameTable) {
        migration(dao, packageName, add, del, true, nameTable);
    }

    /**
     * 为指定package及旗下package中带@Table注解的Pojo执行migration
     *
     * @param dao
     *            Dao实例
     * @param packageName
     *            指定的package名称
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     * @param checkIndex
     *            是否检查索引
     */
    public static void migration(Dao dao,
                                 String packageName,
                                 boolean add,
                                 boolean del,
                                 boolean checkIndex) {
        for (Class<?> klass : Scans.me().scanPackage(packageName)) {
            if (klass.getAnnotation(Table.class) != null) {
                migration(dao, klass, add, del, checkIndex, null);
            }
        }
    }

    /**
     * 为指定package及旗下package中带@Table注解的Pojo执行migration
     *
     * @param dao
     *            Dao实例
     * @param packageName
     *            指定的package名称
     * @param add
     *            是否允许添加字段
     * @param del
     *            是否允许删除字段
     */
    public static void migration(Dao dao, String packageName, boolean add, boolean del) {
        migration(dao, packageName, add, del, true);
    }

    /**
     * 检查分表中是否有字段变化 提示
     *
     * @param dao
     *            Dao实例
     * @param tableName
     *            动态表名上下文
     * @param clsType
     *            映射Pojo
     */
    public static void checkTableColumn(Dao dao, Object tableName, final Class<?> clsType) {
        final NutDao d = (NutDao) dao;
        final JdbcExpert expert = d.getJdbcExpert();
        ext(d, tableName).run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                Entity<?> en = d.getEntity(clsType);
                expert.setupEntityField(conn, en);
            }
        });
    }

    /**
     * 获取动态表的表名
     *
     * @param dao
     *            Dao实例
     * @param klass
     *            映射Pojo
     * @param target
     *            参考对象
     */
    public static String getTableName(Dao dao, Class<?> klass, Object target) {
        return getTableName(dao, dao.getEntity(klass), target);
    }

    /**
     * 获取动态表的表名
     *
     * @param dao
     *            Dao实例
     * @param en
     *            Pojo的数据库实体
     * @param target
     *            参考对象
     */
    public static String getTableName(Dao dao, final Entity<?> en, Object target) {
        if (target == null)
            return en.getTableName();
        final String[] name = new String[1];
        TableName.run(target, new Runnable() {
            public void run() {
                name[0] = en.getTableName();
            }
        });
        return name[0];
    }

    private static SqlFormat sqlFormat = SqlFormat.full;

    /** 获取SQL打印格式 */
    public static SqlFormat getSqlFormat() {
        return sqlFormat;
    }

    /**
     * 设置SQL打印格式
     *
     * @param sqlFormat
     *            SQL打印格式
     */
    public static void setSqlFormat(SqlFormat sqlFormat) {
        Daos.sqlFormat = sqlFormat;
    }

    /** 获取SQL2003关键字 */
    public static Set<String> sql2003Keywords() {
        Set<String> keywords = new HashSet<String>();
        String k = "ADD,ALL,ALLOCATE,ALTER,AND,ANY,ARE,ARRAY,AS,ASENSITIVE,ASYMMETRIC,AT,ATOMIC,AUTHORIZATION,BEGIN,BETWEEN,BIGINT,BINARY,BLOB,BOOLEAN,BOTH,BY,CALL,CALLED,CASCADED,CASE,CAST,CHAR,CHARACTER,CHECK,CLOB,CLOSE,COLLATE,COLUMN,COMMIT,CONDITION,CONNECT,CONSTRAINT,CONTINUE,CORRESPONDING,CREATE,CROSS,CUBE,CURRENT,CURRENT_DATE,CURRENT_DEFAULT_TRANSFORM_GROUP,CURRENT_PATH,CURRENT_ROLE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_TRANSFORM_GROUP_FOR_TYPE,CURRENT_USER,CURSOR,CYCLE,DATE,DAY,DEALLOCATE,DEC,DECIMAL,DECLARE,DEFAULT,DELETE,DEREF,DESCRIBE,DETERMINISTIC,DISCONNECT,DISTINCT,DO,DOUBLE,DROP,DYNAMIC,EACH,ELEMENT,ELSE,ELSEIF,END,ESCAPE,EXCEPT,EXEC,EXECUTE,EXISTS,EXIT,EXTERNAL,FALSE,FETCH,FILTER,FLOAT,FOR,FOREIGN,FREE,FROM,FULL,FUNCTION,GET,GLOBAL,GRANT,GROUP,GROUPING,HANDLER,HAVING,HOLD,HOUR,IDENTITY,IF,IMMEDIATE,IN,INDICATOR,INNER,INOUT,INPUT,INSENSITIVE,INSERT,INT,INTEGER,INTERSECT,INTERVAL,INTO,IS,ITERATE,JOIN,LANGUAGE,LARGE,LATERAL,LEADING,LEAVE,LEFT,LIKE,LOCAL,LOCALTIME,LOCALTIMESTAMP,LOOP,MATCH,MEMBER,MERGE,METHOD,MINUTE,MODIFIES,MODULE,MONTH,MULTISET,NATIONAL,NATURAL,NCHAR,NCLOB,NEW,NO,NONE,NOT,NULL,NUMERIC,OF,OLD,ON,ONLY,OPEN,OR,ORDER,OUT,OUTER,OUTPUT,OVER,OVERLAPS,PARAMETER,PARTITION,PRECISION,PREPARE,PROCEDURE,RANGE,READS,REAL,RECURSIVE,REF,REFERENCES,REFERENCING,RELEASE,REPEAT,RESIGNAL,RESULT,RETURN,RETURNS,REVOKE,RIGHT,ROLLBACK,ROLLUP,ROW,ROWS,SAVEPOINT,SCOPE,SCROLL,SEARCH,SECOND,SELECT,SENSITIVE,SESSION_USER,SET,SIGNAL,SIMILAR,SMALLINT,SOME,SPECIFIC,SPECIFICTYPE,SQL,SQLEXCEPTION,SQLSTATE,SQLWARNING,START,STATIC,SUBMULTISET,SYMMETRIC,SYSTEM,SYSTEM_USER,TABLE,TABLESAMPLE,THEN,TIME,TIMESTAMP,TIMEZONE_HOUR,TIMEZONE_MINUTE,TO,TRAILING,TRANSLATION,TREAT,TRIGGER,TRUE,UNDO,UNION,UNIQUE,UNKNOWN,UNNEST,UNTIL,UPDATE,USER,USING,VALUE,VALUES,VARCHAR,VARYING,WHEN,WHENEVER,WHERE,WHILE,WINDOW,WITH,WITHIN,WITHOUT,YEAR";
        for (String keyword : k.split(",")) {
            keywords.add(keyword);
        }
        keywords.remove("VALUE");
        keywords.remove("SQL");
        keywords.remove("YEAR");
        return keywords;
    }

    /** 是否检查字段为数据库的关键字 */
    public static boolean CHECK_COLUMN_NAME_KEYWORD = false;

    /** 是否把字段名用字符包裹来进行关键字逃逸 */
    public static boolean FORCE_WRAP_COLUMN_NAME = false;

    /** 是否把字段名给变成大写 */
    public static boolean FORCE_UPPER_COLUMN_NAME = false;
    
    public static boolean FORCE_HUMP_COLUMN_NAME = false;

    /** varchar 字段的默认字段长度 */
    public static int DEFAULT_VARCHAR_WIDTH = 128;
    
    /** Table&View名称生成器 */
    public static interface NameMaker {
        String make(Class<?> klass);
    }
    /** 默认的Table名称生成器 */
    private static NameMaker tableNameMaker = new NameMaker() {
        @Override
        public String make(Class<?> klass) {
            return Strings.lowerWord(klass.getSimpleName(), '_');
        }
    };
    /** 默认的View名称生成器 */
    private static NameMaker viewNameMaker = new NameMaker() {
        @Override
        public String make(Class<?> klass) {
            return Strings.lowerWord(klass.getSimpleName(), '_');
        }
    };

    public static NameMaker getTableNameMaker() {
        return tableNameMaker;
    }

    public static void setTableNameMaker(NameMaker tableNameMaker) {
        Daos.tableNameMaker = tableNameMaker;
    }

    public static NameMaker getViewNameMaker() {
        return viewNameMaker;
    }

    public static void setViewNameMaker(NameMaker viewNameMaker) {
        Daos.viewNameMaker = viewNameMaker;
    }
}

class ExtDaoInvocationHandler implements InvocationHandler {

    protected ExtDaoInvocationHandler(Dao dao, FieldFilter filter, Object tableName) {
        this.dao = dao;
        this.filter = filter;
        this.tableName = tableName;
    }

    public Dao dao;
    public FieldFilter filter;
    public Object tableName;

    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {

        final Molecule<Object> m = new Molecule<Object>() {
            public void run() {
                try {
                    setObj(method.invoke(dao, args));
                }
                catch (Exception e) {
                    throw Lang.wrapThrow(e);
                }
            }
        };
        if (filter != null && tableName != null) {
            TableName.run(tableName, new Runnable() {
                public void run() {
                    filter.run(m);
                }
            });
            return m.getObj();
        }
        if (filter != null)
            filter.run(m);
        else
            TableName.run(tableName, m);
        return m.getObj();
    }
}
