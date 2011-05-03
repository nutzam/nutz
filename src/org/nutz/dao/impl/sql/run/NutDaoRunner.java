package org.nutz.dao.impl.sql.run;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DaoException;
import org.nutz.dao.impl.DaoRunner;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

public class NutDaoRunner implements DaoRunner {

	private static final Log log = Logs.get();

	public void run(DataSource dataSource, ConnCallback callback) {
		Transaction t = Trans.get();
		// 有事务
		if (null != t) {
			try {
				Connection conn = t.getConnection(dataSource);
				callback.invoke(conn);
			}
			catch (Exception e) {
				throw new DaoException(e);
			}
		}
		// 无事务
		else {
			Connection conn = null;
			boolean old = false;
			// 开始一个连接
			try {
				conn = dataSource.getConnection();
				// 多条语句运行，将自动提交设为 false
				old = conn.getAutoCommit();
				conn.setAutoCommit(false);
				// 开始循环运行
				callback.invoke(conn);
				// 完成提交
				if (!conn.getAutoCommit())
					conn.commit();
			}
			// 异常回滚
			catch (Exception e) {
				try {
					conn.rollback();
				}
				catch (SQLException e1) {}
				throw new DaoException(e);
			}
			// 保证释放资源
			finally {
				if (null != conn) {
					// 恢复链接自动提交设定
					try {
						if (old != conn.getAutoCommit())
							conn.setAutoCommit(old);
					}
					catch (SQLException autoE) {
						if (log.isWarnEnabled())
							log.warn("Fail to restore autoCommet to '" + old + "'", autoE);
					}
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
