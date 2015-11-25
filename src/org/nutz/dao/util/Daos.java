package org.nutz.dao.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
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
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityIndex;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
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
        log.infof("Can not find @Column(%s) in table/view (%s)", colName, meta.getTableName(1));
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
                for (T t : list)
                    dao.fetchLinks(t, regex);
                setObj(list);
            }
        };
        return Trans.exec(molecule);
    }

    /** 根据Pojo生成数据字典,zdoc格式 */
    public static StringBuilder dataDict(DataSource ds, String... packages) {
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

        JdbcExpert exp = Jdbcs.getExpert(ds);
        NutDao dao = new NutDao(ds);

        Method evalFieldType;
        try {
            evalFieldType = exp.getClass().getDeclaredMethod("evalFieldType", MappingField.class);
        }
        catch (Throwable e) {
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
                }
                catch (Throwable e) {
                    throw Lang.wrapThrow(e); // 不可能发生的
                }
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
     * 查询某sql的结果条数
     */
    public static long queryCount(Dao dao, String sql) {
        Sql sql2 = Sqls.fetchInt("select count(1) from ("
                                 + sql
                                 + ") as _nutz_tmp_"
                                 + System.currentTimeMillis());
        dao.execute(sql2);
        return sql2.getInt();
    }

    /**
     * 执行一个特殊的Chain(事实上普通Chain也能执行,但不建议使用)
     * 
     * @see org.nutz.dao.Chain#addSpecial(String, Object)
     */
    @SuppressWarnings({"rawtypes"})
    public static int updateBySpecialChain(Dao dao,
                                           Entity en,
                                           String tableName,
                                           Chain chain,
                                           Condition cnd) {
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
                if (head.value() != null && head.value() instanceof String) {
                    String str = (String) head.value();
                    if (str.length() > 0) {
                        switch (str.charAt(0)) {
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '%':
                        case '&':
                        case '^':
                        case '|':
                            sql.append(colName);
                            break;
                        }
                    }
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
                }
                finally {
                    Daos.safeClose(ps);
                }
            }
        });
        return ints[0];
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
                    colName = mf.getColumnName();
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
     * 为特定package下带@Table注解的类调用dao.create(XXX.class, force), 批量建表
     * 
     * @param dao
     *            Dao实例
     * @param packageName
     *            package名称,自动包含子类
     * @param force
     *            如果表存在,是否先删后建
     */
    public static void createTablesInPackage(Dao dao, String packageName, boolean force) {
        for (Class<?> klass : Scans.me().scanPackage(packageName)) {
            if (klass.getAnnotation(Table.class) != null)
                dao.create(klass, force);
        }
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

        List<MappingField> mfs = en.getMappingFields();
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
                if (matcher.isIgnoreZero() && val instanceof Number && ((Number) val).doubleValue() == 0.0) {
                    continue;
                }
                if (matcher.isIgnoreDate() && val instanceof Date) {
                    continue;
                }
                if (matcher.isIgnoreBlankStr() && val instanceof String) {
                
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
     *            是否允许添加
     * @param del
     *            是否允许删除
     */
    public static void migration(Dao dao, final Class<?> klass, final boolean add, final boolean del) {
        migration(dao, klass, add, del, null);
    }

    /**
     * 为数据表自动增减字段
     * 
     * @param dao
     *            Dao实例
     * @param klass
     *            映射Pojo
     * @param add
     *            是否允许添加
     * @param del
     *            是否允许删除
     * @param tableName
     *            动态表名上下文
     */
    public static void migration(Dao dao,
                                 final Class<?> klass,
                                 final boolean add,
                                 final boolean del,
                                 Object tableName) {
        final AbstractJdbcExpert expert = (AbstractJdbcExpert) ((NutDao) dao).getJdbcExpert();
        if (tableName != null && Strings.isNotBlank(tableName.toString())) {
            dao = ext(dao, tableName);
        }
        final Entity<?> en = dao.getEntity(klass);
        if (!dao.exists(klass))
            return;
        final List<Sql> sqls = new ArrayList<Sql>();
        final Set<String> _indexs = new HashSet<String>();
        final boolean sqlAddNeedColumn = !dao.meta().isOracle();
        final boolean isCanComment = dao.meta().isMySql();
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
                            StringBuilder sb = new StringBuilder("ALTER TABLE ");
                            sb.append(meta.getTableName(1)).append(" ADD ");
                            if (sqlAddNeedColumn)
                                sb.append("COLUMN ");
                            sb.append(colName).append(" ").append(expert.evalFieldType(mf));
                            if (mf.isUnsigned()) {
                                sb.append(" UNSIGNED");
                            }
                            if (mf.isNotNull()) {
                                sb.append(" NOT NULL");
                            }
                            if (mf.getColumnType() == ColType.TIMESTAMP) {
                                if (mf.hasDefaultValue()) {
                                    sb.append(" ")
                                      .append(mf.getDefaultValue(null).replaceAll("@", "@@"));
                                } else {
                                    if (mf.isNotNull()) {
                                        sb.append(" DEFAULT 0");
                                    } else {
                                        sb.append(" NULL DEFAULT NULL");
                                    }
                                }
                            } else {
                                if (mf.hasDefaultValue())
                                    expert.addDefaultValue(sb, mf);
                            }
                            if (mf.hasColumnComment() && isCanComment) {
                                sb.append(" COMMENT '").append(mf.getColumnComment()).append("'");
                            }
                            sb.append(';');
                            Sql sql = Sqls.create(sb.toString());
                            sqls.add(sql);
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
                    String showIndexs = "show index from " + en.getTableName();
                    PreparedStatement ppstat = conn.prepareStatement(showIndexs);
                    ResultSet rest = ppstat.executeQuery();
                    while (rest.next()) {
                    	String index = rest.getString(3);
                        _indexs.add(index);
                    }
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
        for (Sql sql : sqls) {
            dao.execute(sql);
        }
        // 创建索引
        List<Sql> indexsSql = createIndexs(dao,en, _indexs,tableName);
        if (!Lang.isEmpty(indexsSql)) {
            dao.execute(indexsSql.toArray(new Sql[0]));
        }
        // 创建关联表
        expert.createRelation(dao, en);
    }
    private static List<Sql> createIndexs(Dao dao,Entity<?> en, Set<String> indexsHis,Object t) {
        List<Sql> sqls = new ArrayList<Sql>();
        StringBuilder sb = new StringBuilder();
        List<EntityIndex> indexs = en.getIndexes();
        for (EntityIndex index : indexs) {
            if (indexsHis.contains(index.getName())) {
                continue;
            }
            sb.setLength(0);
            if (index.isUnique())
                sb.append("Create UNIQUE Index ");
            else
                sb.append("Create Index ");
            if (index.getName().contains("$"))
                sb.append(TableName.render(new CharSegment(index.getName())));
            else
                sb.append(index.getName());
            sb.append(" ON ").append(getTableName(dao, en, t)).append("(");
            for (EntityField field : index.getFields()) {
                if (field instanceof MappingField) {
                    MappingField mf = (MappingField) field;
                    sb.append(mf.getColumnName()).append(',');
                } else {
                    throw Lang.makeThrow(DaoException.class,
                                         "%s %s is NOT a mapping field, can't use as index field!!",
                                         en.getClass(),
                                         field.getName());
                }
            }
            sb.setCharAt(sb.length() - 1, ')');
            sqls.add(Sqls.create(sb.toString()));
        }
        return sqls;
    }

    /**
     * 为指定package及旗下package中带@Table注解的Pojo执行migration
     * 
     * @param dao
     *            Dao实例
     * @param packageName
     *            指定的package名称
     * @param add
     *            是否允许添加
     * @param del
     *            是否允许删除
     * @param nameTable
     *            动态表名上下文
     */
    public static void migration(Dao dao,
                                 String packageName,
                                 boolean add,
                                 boolean del,
                                 String nameTable) {
        for (Class<?> klass : Scans.me().scanPackage(packageName)) {
            if (klass.getAnnotation(Table.class) != null) {
                migration(dao, klass, add, del, nameTable);
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
     *            是否允许添加
     * @param del
     *            是否允许删除
     */
    public static void migration(Dao dao, String packageName, boolean add, boolean del) {
        for (Class<?> klass : Scans.me().scanPackage(packageName)) {
            if (klass.getAnnotation(Table.class) != null) {
                migration(dao, klass, add, del, null);
            }
        }
    }

    /**
     * 检查分表中是否有字段变化 提示
     * 
     * @param dao
     * @param tableName
     * @param clsType
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
     */
    public static String getTableName(Dao dao, Class<?> klass, Object t) {
        return getTableName(dao, dao.getEntity(klass), t);
    }
    
    public static String getTableName(Dao dao, final Entity<?> en, Object t) {
        if (t == null)
            return en.getTableName();
        final String[] name = new String[1];
        TableName.run(t, new Runnable() {
            public void run() {
                name[0] = en.getTableName();
            }
        });
        return name[0];
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
                catch (IllegalArgumentException e) {
                    throw Lang.wrapThrow(e);
                }
                catch (IllegalAccessException e) {
                    throw Lang.wrapThrow(e);
                }
                catch (InvocationTargetException e) {
                    throw Lang.wrapThrow(e.getTargetException());
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