package org.nutz.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.SqlManager;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.entity.AnnotationEntityMaker;
import org.nutz.dao.impl.sql.NutPojoMaker;
import org.nutz.dao.impl.sql.run.NutDaoExecutor;
import org.nutz.dao.impl.sql.run.NutDaoRunner;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.PojoMaker;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlContext;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

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
    private DatabaseMeta meta;

    /**
     * SQL 管理接口实现类
     */
    private SqlManager sqlManager;
    
    protected int autoTransLevel = Connection.TRANSACTION_READ_COMMITTED;

    public DaoSupport() {
        this.runner = new NutDaoRunner();
        this.executor = new NutDaoExecutor();
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
        if (null != dataSource)
            if (log.isWarnEnabled())
                log.warn("Replaced a running dataSource!");
        dataSource = ds;
        if (expert == null)
            expert = Jdbcs.getExpert(ds);
        pojoMaker = new NutPojoMaker(expert);

        meta = new DatabaseMeta();
        run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
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
                if (log.isDebugEnabled() && meta.isMySql()) {
                    String sql = "SHOW VARIABLES LIKE 'character_set%'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next())
                        log.debugf("Mysql : %s=%s", rs.getString(1), rs.getString(2));
                    rs.close();
                    // 打印当前数据库名称
                    String dbName = "";
                    rs = stmt.executeQuery("SELECT DATABASE()");
                    if (rs.next()) {
                    	dbName = rs.getString(1);
                    	log.debug("Mysql : database=" + dbName);
                    }
                    rs.close();
                    // 打印当前连接用户及主机名
                    rs = stmt.executeQuery("SELECT USER()");
                    if (rs.next())
                    	log.debug("Mysql : user=" + rs.getString(1));
                    rs.close();
                    stmt.close();
                    // 列出所有MyISAM引擎的表,这些表不支持事务
                    PreparedStatement pstmt = conn.prepareStatement("SELECT TABLE_NAME FROM information_schema.TABLES where TABLE_SCHEMA = ? and engine = 'MyISAM'");
                    pstmt.setString(1, dbName);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    	log.debug("Mysql : '"+rs.getString(1) + "' engine=MyISAM");
                    rs.close();
                    pstmt.close();
                }
            }
        });
        if (log.isDebugEnabled())
            log.debug("Database info --> " + meta);

        holder = new EntityHolder(this);
        holder.maker = createEntityMaker();
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
        // 看看是不是都是 SELECT 语句
        boolean isAllSelect = true;
        for (DaoStatement st : sts) {
            if (!st.isSelect()) {
                isAllSelect = false;
                break;
            }
        }
        // 这个是具体执行的逻辑，作为一个回调
        // 后面的逻辑是判断到底应不应该在一个事务里运行它
        DaoExec callback = new DaoExec(sts);

        // 如果强制没有事务或者都是 SELECT，没必要启动事务
        boolean useTrans = false;
        switch (meta.getType()) {
        case PSQL:
            useTrans = true;
            break;
        case SQLITE:
            Transaction t = Trans.get();
            useTrans = (t != null && (t.getLevel() == Connection.TRANSACTION_SERIALIZABLE
                                   || t.getLevel() == Connection.TRANSACTION_READ_UNCOMMITTED));
            break;
        default:
            useTrans = !(Trans.isTransactionNone() && (sts.length==1 || isAllSelect));
            break;
        }
        if (!useTrans) {
            run(callback);
        }
        // 否则启动事务
        // wendal: 还是很有必要的!!尤其是解决insert的@Prev/@Next不在同一个链接的问题
        else {
            Trans.exec(autoTransLevel, callback);
        }

        // 搞定，返回结果 ^_^
        return callback.re;
    }

    /**
     * 子类可以重写这个类，用来扩展成其他的实体配置方式
     * 
     * @return 实体工厂
     */
    protected EntityMaker createEntityMaker() {
        return new AnnotationEntityMaker(dataSource, expert, holder);
    }

    /**
     * 
     * @author wendal
     * @since 1.b.44
     */
    protected class DaoExec implements Atom, ConnCallback {
        private DaoStatement[] sts;
        private int re;

        public DaoExec(DaoStatement... sts) {
            this.sts = sts;
        }

        public void run() {
            DaoSupport.this.run(this);
        }

        public void invoke(Connection conn) throws Exception {
            for (DaoStatement st : sts) {
                if (st == null) {
                    if (log.isInfoEnabled())
                        log.info("Found a null DaoStatement(SQL), ingore it ~~");
                    continue;
                }
                executor.exec(conn, st);
                re += st.getUpdateCount();
            }
        }
    }
    
    public PojoMaker pojoMaker() {
		return pojoMaker;
	}
    
    public void setAutoTransLevel(int autoTransLevel) {
        this.autoTransLevel = autoTransLevel;
    }
}
