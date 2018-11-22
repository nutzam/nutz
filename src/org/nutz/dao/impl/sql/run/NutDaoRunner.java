package org.nutz.dao.impl.sql.run;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.impl.DaoRunner;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

/**
 * 统管事务和拦截链
 * @author wendal
 *
 */
public class NutDaoRunner implements DaoRunner {

    private static final Log log = Logs.get();
    
    protected DataSource slaveDataSource;
    
    public void run(final DataSource dataSource, final ConnCallback callback) {
        if (callback instanceof DaoInterceptorChain) {
            DaoInterceptorChain chain = (DaoInterceptorChain)callback;
            // 看看是不是应该强制使用事务
            DaoStatement[] sts = chain.getDaoStatements();
            boolean useTrans = false;
            boolean isAllSelect = true;
            for (DaoStatement st : sts) {
                if (!st.isSelect() && !st.isForceExecQuery()) {
                    isAllSelect = false;
                    break;
                }
            }
            switch (meta.getType()) {
            case PSQL:
                // PSQL必须带事务,不然Clob和Blob操作必死
                useTrans = true;
                break;
            case SQLITE:
                // SQLITE仅支持2种事务级别
                Transaction t = Trans.get();
                if (t == null) {
                    if (isAllSelect)
                        useTrans = false;
                    else {
                        chain.setAutoTransLevel(Connection.TRANSACTION_READ_UNCOMMITTED);
                        useTrans = true;
                    }
                }
                else if (t.getLevel() != Connection.TRANSACTION_SERIALIZABLE
                                       && t.getLevel() != Connection.TRANSACTION_READ_UNCOMMITTED) {
                    t.setLevel(Connection.TRANSACTION_READ_UNCOMMITTED);
                    useTrans = true;
                }
                break;
            default:
                useTrans = !(Trans.isTransactionNone() && (sts.length==1 || isAllSelect));
                break;
            }
            // 看来需要开启事务了
            if (useTrans && chain.getAutoTransLevel() > 0) {
                Trans.exec(chain.getAutoTransLevel(), new Atom() {
                    public void run() {
                        _run(dataSource, callback);
                    }
                });
                return;
            }
        }
        // 不需要额外加事务,直接通过
        _run(dataSource, callback);
    }
    
    public void _run(DataSource dataSource, ConnCallback callback) {
        Transaction t = Trans.get();
        // 有事务
        if (null != t) {
            _runWithTransaction(t, dataSource, callback);
        }
        // 无事务
        else {
            _runWithoutTransaction(dataSource, callback);
        }
    }
    
    protected void _runWithTransaction(Transaction t, DataSource dataSource, ConnCallback callback) {
        Connection conn = null;
        Savepoint sp = null;
        try {
            conn = t.getConnection(selectDataSource(t, dataSource, callback));
            if (meta != null && meta.isPostgresql()) {
                sp = conn.setSavepoint();
            }
            runCallback(conn, callback);
        }
        catch (Exception e) {
            if (sp != null && conn != null)
                try {
                    conn.rollback(sp);
                }
                catch (SQLException e1) {
                }
            if (e instanceof DaoException)
                throw (DaoException)e;
            throw new DaoException(e);
        }
    }
    
    public void _runWithoutTransaction(DataSource dataSource, ConnCallback callback) {
        Connection conn = null;
        // 开始一个连接
        try {
            conn = selectDataSource(null, dataSource, callback).getConnection();
            // 开始真正运行
            runCallback(conn, callback);
            // 完成提交
            if (!conn.getAutoCommit())
                conn.commit();
        }
        // 异常回滚
        catch (Exception e) {
            try {
                if (conn != null) // 高并发时,从数据库连接池获取连接就已经抛错误,所以conn可能为null的
                    conn.rollback();
            }
            catch (Exception e1) {}// TODO 简单记录一下?
            if (e instanceof DaoException)
                throw (DaoException)e;
            throw new DaoException(e);
        }
        // 保证释放资源
        finally {
            if (null != conn) {
                // 关闭链接
                try {
                    conn.close();
                }
                catch (SQLException closeE) {
                    if (log.isWarnEnabled())
                        log.warn("Fail to close connection!", closeE);
                }
            }
        }
    }

    
    protected void runCallback(Connection conn, ConnCallback callback) throws Exception {
        callback.invoke(conn);
    }
    
    protected DatabaseMeta meta;
    
    public void setMeta(DatabaseMeta meta) {
		this.meta = meta;
	}
    
    public void setSlaveDataSource(DataSource slaveDataSource) {
        this.slaveDataSource = slaveDataSource;
    }
    
    protected DataSource selectDataSource(Transaction t, DataSource master, ConnCallback callback) {
        if (this.slaveDataSource == null)
            return master;
        if (t == null && callback instanceof DaoInterceptorChain) {
            DaoInterceptorChain chain = (DaoInterceptorChain)callback;
            DaoStatement[] sts = chain.getDaoStatements();
            if (sts.length == 1 && (sts[0].isSelect() || sts[0].isForceExecQuery())) {
                return slaveDataSource;
            }
        }
        return master;
    }
}
