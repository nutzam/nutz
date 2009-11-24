package org.nutz.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.lang.Lang;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

/**
 * Dao 的帮助函数，基本上，你不会用到这个类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Daos {

	/**
	 * 获取连接
	 * 
	 * @param dataSource 数据源
	 * @return 连接持有者
	 */
	public static ConnectionHolder getConnection(DataSource dataSource) {
		try {
			Transaction trans = Trans.get();
			Connection conn = null;
			if (trans != null)
				conn = trans.getConnection(dataSource);
			else
				conn = dataSource.getConnection();
			return ConnectionHolder.make(trans, conn);
		} catch (SQLException e) {
			throw Lang.makeThrow("Could not get JDBC Connection : %s", e.getMessage());
		}
	}

	/**
	 * 释放连接
	 * 
	 * @param ch 连接持有者
	 */
	public static void releaseConnection(ConnectionHolder ch) {
		try {
			ch.close();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
}
