package org.nutz.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

import org.nutz.dao.pager.Pager;
import org.nutz.lang.Lang;
import org.nutz.service.EntityService;
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
	 * @param dataSource
	 *            数据源
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
		}
		catch (SQLException e) {
			throw Lang.makeThrow("Could not get JDBC Connection : %s", e.getMessage());
		}
	}

	/**
	 * 释放连接
	 * 
	 * @param ch
	 *            连接持有者
	 */
	public static void releaseConnection(ConnectionHolder ch) {
		try {
			ch.close();
		}
		catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static int getColumnIndex(ResultSetMetaData meta, String colName) throws SQLException {
		int ci = 0;
		if (null != meta) {
			int columnCount = meta.getColumnCount();
			for (int i = 1; i <= columnCount; i++)
				if (meta.getColumnName(i).equalsIgnoreCase(colName)) {
					ci = i;
					break;
				}
			if (ci == 0)
				throw Lang.makeThrow(SQLException.class, "Can not find @Column(%s)", colName);
		}
		return ci;
	}

	public static boolean isIntLikeColumn(ResultSetMetaData meta, String colName)
			throws SQLException {
		return isIntLikeColumn(meta, getColumnIndex(meta, colName));
	}

	public static boolean isIntLikeColumn(ResultSetMetaData meta, int index) throws SQLException {
		boolean isIntLike = false;
		int colType = meta.getColumnType(index);
		switch (colType) {
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.NUMERIC:
			isIntLike = true;
		}
		return isIntLike;
	}

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

	public static Pager updatePagerCount(Pager pager, EntityService<?> srv, Condition cnd) {
		if (null != pager)
			pager.setRecordCount(srv.count(cnd));
		return pager;
	}
}
