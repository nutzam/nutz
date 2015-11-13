package org.nutz.dao.impl.sql.run;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DaoException;
import org.nutz.dao.impl.DaoRunner;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

/**
 * 1.r.54开始移除auto-commit相关的代码
 * @author wendal
 *
 */
public class NutDaoRunner implements DaoRunner {

    private static final Log log = Logs.get();

    public void run(DataSource dataSource, ConnCallback callback) {
        Transaction t = Trans.get();
        // 有事务
        if (null != t) {
            Connection conn = null;
            Savepoint sp = null;
            try {
                conn = t.getConnection(dataSource);
                sp = conn.setSavepoint();
                callback.invoke(conn);
            }
            catch (Exception e) {
                if (e instanceof DaoException)
                    if (null != conn
                        && null != e.getCause()
                        && e.getCause() instanceof SQLException) {
                        try {
                            if (null == sp)
                                conn.rollback();
                            else
                                conn.rollback(sp);
                        }
                        catch (SQLException e1) {
                            if (log.isErrorEnabled())
                                log.error(e1);
                        }
                    }
                throw new DaoException(Lang.unwrapThrow(e));
            }
        }
        // 无事务
        else {
            Connection conn = null;
            // 开始一个连接
            try {
                conn = dataSource.getConnection();
                // 开始循环运行
                callback.invoke(conn);
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
    }
}
