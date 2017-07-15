package org.nutz.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.SqlManager;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.entity.AnnotationEntityMaker;
import org.nutz.dao.impl.interceptor.DaoLogInterceptor;
import org.nutz.dao.impl.interceptor.DaoTimeInterceptor;
import org.nutz.dao.impl.sql.NutPojoMaker;
import org.nutz.dao.impl.sql.run.NutDaoExecutor;
import org.nutz.dao.impl.sql.run.NutDaoRunner;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.PojoMaker;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlContext;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Dao 接口实现类的一些基础环境
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class DaoSupport {

    private static final Log log = Logs.get();

    /**
     * 给子类使用的 Dao 的运行器，用来封装事务
     */
    protected DaoRunner runner;

    /**
     * 给子类使用的 Dao 语句执行器，用来具体运行某一条语句
     */
    protected DaoExecutor executor;

    /**
     * 给子类使用数据源
     */
    protected DataSource dataSource;

    /**
     * 给子类使用的数据特殊性的封装
     */
    protected JdbcExpert expert;

    /**
     * 给子类使用的 PojoStatementMaker 接口
     */
    protected PojoMaker pojoMaker;

    /**
     * 给子类使用的 Entity 获取对象
     */
    protected EntityHolder holder;

    /**
     * 数据库的描述
     */
    protected DatabaseMeta meta;

    /**
     * SQL 管理接口实现类
     */
    protected SqlManager sqlManager;
    
    protected int autoTransLevel = Connection.TRANSACTION_READ_COMMITTED;
    
    protected List<DaoInterceptor> _interceptors;

    public DaoSupport() {
        this.runner = new NutDaoRunner();
        this.executor = new NutDaoExecutor();
        this.setInterceptors(Lang.list((Object)"log"));
    }

    /**
     * @return Sql 管理接口的实例
     */
    public SqlManager sqls() {
        return sqlManager;
    }

    /**
     * @return 当前连接的数据库的一些描述数据
     */
    public DatabaseMeta meta() {
        return meta;
    }

    /**
     * 设置一个新的 Sql 管理接口实例
     * 
     * @param sqls
     *            Sql 管理接口实例
     */
    public void setSqlManager(SqlManager sqls) {
        this.sqlManager = sqls;
        if (sqls != null) {
            int count = sqls.count();
            log.debug("SqlManager Sql count=" + count);
        }
    }

    /**
     * 设置一个新的 Dao 运行器
     * 
     * @param runner
     *            运行器对象
     */
    public void setRunner(DaoRunner runner) {
        this.runner = runner;
        if (runner instanceof NutDaoRunner) {
        	((NutDaoRunner)runner).setMeta(meta);
        }
    }

    /**
     * 设置一个新的 Dao 语句执行器
     * 
     * @param executor
     *            Dao 语句执行器对象
     */
    public void setExecutor(DaoExecutor executor) {
        this.executor = executor;
        if (executor instanceof NutDaoExecutor) {
        	((NutDaoExecutor)executor).setMeta(meta);
        	((NutDaoExecutor)executor).setExpert(expert);
        }
    }

    /**
     * 设置一个新的 Pojo 语句创建器
     * 
     * @param pojoMaker
     *            Pojo 语句创建器
     */
    public void setPojoMaker(PojoMaker pojoMaker) {
        this.pojoMaker = pojoMaker;
    }

    /**
     * @return 当前的 JDBC 专家类
     */
    public JdbcExpert getJdbcExpert() {
        return expert;
    }

    /**
     * 设置新的数据源。
     * <p>
     * 如果有老的数据源需要你在外部手动关闭
     * 
     * @param ds
     *            数据源
     */
    public void setDataSource(DataSource ds) {
        setDataSource(ds,false);
    }
    
    public void setDataSource(DataSource ds,boolean isLazy) {
        if (null != dataSource)
            if (log.isWarnEnabled())
                log.warn("Replaced a running dataSource!");
        dataSource = ds;
        if (expert == null)
            expert = Jdbcs.getExpert(ds);
        log.debug("select expert : " + expert.getClass().getName());
        pojoMaker = new NutPojoMaker(expert);

        meta = new DatabaseMeta();
        final Set<String> keywords = new HashSet<String>(Daos.sql2003Keywords());
        run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                try {
                    DatabaseMetaData dmd = conn.getMetaData();
                    meta.setProductName(dmd.getDatabaseProductName());
                    meta.setVersion(dmd.getDatabaseProductVersion());
                    log.debug("JDBC Driver --> " + dmd.getDriverVersion());
                    log.debug("JDBC Name   --> " + dmd.getDriverName());
                    if (!Strings.isBlank(dmd.getURL()))
                        log.debug("JDBC URL    --> " + dmd.getURL());
                    if (dmd.getDriverName().contains("mariadb") || dmd.getDriverName().contains("sqlite")) {
                        log.warn("Auto-select fetch size to Integer.MIN_VALUE, enable for ResultSet Streaming");
                        SqlContext.DEFAULT_FETCH_SIZE = Integer.MIN_VALUE;
                    }
                    String tmp = dmd.getSQLKeywords();
                    if (tmp != null) {
                        for (String keyword : tmp.split(",")) {
                            keywords.add(keyword.toUpperCase());
                        }
                    }
                    expert.checkDataSource(conn);
                }
                catch (Exception e) {
                    log.info("something wrong when checking DataSource", e);
                }
            }
        });
        if (log.isDebugEnabled())
            log.debug("Database info --> " + meta);
        expert.setKeywords(keywords);

        if(!isLazy)
        {
            holder = new EntityHolder(this.expert, dataSource);
            holder.maker = createEntityMaker();
        }
        setRunner(runner);
        setExecutor(executor);
    }

    public void execute(final Sql... sqls) {
        for (Sql sql : sqls)
            expert.formatQuery(sql);
        _exec(sqls);
    }

    public void run(ConnCallback callback) {
        runner.run(dataSource, callback);
    }

    protected int _exec(final DaoStatement... sts) {
        if (sts != null)
            for (DaoStatement ds : sts) {
                ds.setExpert(expert);
            }
        final DaoInterceptorChain callback = new DaoInterceptorChain(sts);
        callback.setExecutor(executor);
        callback.setAutoTransLevel(autoTransLevel);
        callback.setInterceptors(Collections.unmodifiableList(this._interceptors));
        run(callback);
        // 搞定，返回结果 ^_^
        return callback.getUpdateCount();
    }

    /**
     * 子类可以重写这个类，用来扩展成其他的实体配置方式
     * 
     * @return 实体工厂
     */
    protected EntityMaker createEntityMaker() {
        return new AnnotationEntityMaker(dataSource, expert, holder);
    }
    
    public PojoMaker pojoMaker() {
		return pojoMaker;
	}
    
    public void setAutoTransLevel(int autoTransLevel) {
        this.autoTransLevel = autoTransLevel;
    }
    
    public void setInterceptors(List<Object> interceptors) {
        List<DaoInterceptor> list = new LinkedList<DaoInterceptor>();
        for (Object it : interceptors) {
            DaoInterceptor d = makeInterceptor(it);
            if (d != null)
                list.add(d);
        }
        this._interceptors = list;
    }
    
    public void addInterceptor(Object it) {
        DaoInterceptor d = makeInterceptor(it);
        if (d != null) {
            List<DaoInterceptor> list = new LinkedList<DaoInterceptor>(this._interceptors);
            list.add(d);
            this._interceptors = list;
        }
    }
    
    public DaoInterceptor makeInterceptor(Object it) {
        if (it == null)
            return null;
        if (it instanceof String) {
            String itName = it.toString().trim();
            if ("log".equals(itName)) {
                return new DaoLogInterceptor();
            }
            else if ("time".equals(itName)) {
                return new DaoTimeInterceptor();
            } 
            else if (itName.contains(".")) {
                Class<?> klass = Lang.loadClassQuite(itName);
                if (klass == null) {
                    log.warn("no such interceptor name="+itName);
                } else {
                    return (DaoInterceptor) Mirror.me(klass).born();
                }
            } else {
                log.info("unkown interceptor name="+itName);
            }
        }
        else if (it instanceof DaoInterceptor) {
            return (DaoInterceptor) it;
        } else {
            log.info("unkown interceptor -> "+it);
        }
        return null;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
}
