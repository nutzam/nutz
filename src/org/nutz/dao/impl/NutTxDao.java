package org.nutz.dao.impl;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.impl.sql.run.NutDaoRunner;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 独立于Trans事务的Dao实例. 本实例不是线程安全的,不可以在不同线程中同时操作, 但可以重复使用.
 * 
 * @author wendal
 *
 */
public class NutTxDao extends NutDao implements Closeable {

    private static final Log log = Logs.get();

    protected Connection conn;

    protected String id;

    protected boolean debug;

    /**
     * 是否恢复conn原本的auto commit设置
     */
    protected boolean _autoCommit;

    protected NutMap sps;

    /**
     * 通过NutDao实例构建一个独立事务的Dao实例
     * 
     * @param _dao
     *            参数类型是Dao,但必须是NutDao实例
     * @throws DaoException
     *             获取数据连接失败时会抛出
     */
    public NutTxDao(Dao _dao) throws DaoException {
        NutDao dao = (NutDao) _dao;
        // ---------------------------------
        // 下面两个属性在1.r.59及之前的版本没有开放,但仍可以通过Mirror.set/get的方法操作
        this.meta = dao.meta;
        this.sqlManager = dao.sqlManager;
        // ---------------------------------
        this.dataSource = dao.dataSource;
        this.executor = dao.executor;
        this.dataSource = dao.dataSource;
        this.expert = dao.expert;
        this.pojoMaker = dao.pojoMaker;
        this.holder = dao.holder;
        this.autoTransLevel = dao.autoTransLevel;
        this._interceptors = dao._interceptors;
        this.setRunner(new NutDaoRunner() {
            public void _run(DataSource dataSource, ConnCallback callback) {
                try {
                    runCallback(getConnection(), callback);
                }
                catch (Exception e) {
                    if (e instanceof RuntimeException)
                        throw (RuntimeException) e;
                    throw new DaoException(e);
                }
            }
        });
        sps = new NutMap();
    }

    /**
     * 开启事务,级别为READ_COMMITTED
     * 
     * @return 原对象
     */
    public NutTxDao beginRC() {
        return begin(Connection.TRANSACTION_READ_COMMITTED);
    }

    /**
     * 开启事务,级别为SERIALIZABLE
     * 
     * @return 原对象
     */
    public NutTxDao beginSE() {
        return begin(Connection.TRANSACTION_SERIALIZABLE);
    }

    /**
     * 开启事务
     * 
     * @param transLevel
     *            事务级别
     * @see java.sql.Connection
     * @return 原对象
     * @throws DaoException
     *             如果已经开启过事务
     */
    public NutTxDao begin(int transLevel) throws DaoException {
        if (this.conn != null)
            throw new DaoException("NutTxDao has been begined!!");
        id = R.UU32();
        if (debug)
            log.debugf("begin level=%d id=%s", transLevel, id);
        try {
            this.conn = dataSource.getConnection();
            this.conn.setTransactionIsolation(transLevel);
            if (this.conn.getAutoCommit() == true) {
                this.conn.setAutoCommit(false);
                _autoCommit = true;
            }
            setSavepoint(id);
        }
        catch (SQLException e) {
            throw new DaoException(e);
        }
        return this;
    }

    /**
     * 提交事务
     * 
     * @return 原对象
     */
    public NutTxDao commit() {
        if (debug)
            log.debugf("commit id=%s", id);
        try {
            conn.commit();
        }
        catch (SQLException e) {
            throw new DaoException(e);
        }
        return this;
    }

    /**
     * 回滚事务
     * 
     * @return 原对象
     */
    public NutTxDao rollback() {
        return rollback(id);
    }

    /**
     * 回滚事务
     * 
     * @param id
     *            回滚点
     * @return 原对象
     */
    public NutTxDao rollback(String id) {
        if (debug)
            log.debugf("rollback id=%s", id);
        try {
            Savepoint sp = sps.getAs(id, Savepoint.class);
            if (sp != null)
                conn.rollback(sp);
            else
                log.debug("Null Savepoint found, skip, id=" + id);
        }
        catch (Throwable e) {
        }
        return this;
    }

    public NutTxDao setSavepoint(String spId) {
        try {
            sps.put(spId, conn.setSavepoint());
        }
        catch (SQLException e) {
            throw new DaoException(e);
        }
        return this;
    }

    /**
     * 关闭事务及连接
     */
    public void close() {
        if (debug)
            log.debugf("close id=%s", id);
        try {
            if (conn == null)
                return;
            if (_autoCommit)
                conn.setAutoCommit(true);
            conn.close();
            conn = null;
        }
        catch (Throwable e) {
        }
    }

    /**
     * 获取当前事务的数据库连接
     * 
     * @return 数据库连接
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * 本事务的id
     * 
     * @return 一个字符串
     */
    public String getId() {
        return id;
    }

    /**
     * 设置debug默认
     * 
     * @param debug
     *            是否开启debug日志
     */
    public NutTxDao setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
