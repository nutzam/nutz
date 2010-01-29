package org.nutz.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.nutz.trans.Transaction;

/**
 * 持有一个 Connction 对象。 它记录 Connection 原始的状态， 当关闭的时候，还原
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ConnectionHolder {

	public static ConnectionHolder make(Transaction trans, Connection conn) throws SQLException {
		ConnectionHolder ch = new ConnectionHolder();
		ch.trans = trans;
		ch.conn = conn;
		ch.auto = conn.getAutoCommit();
		return ch;
	}

	private ConnectionHolder() {}

	private Connection conn;
	private Transaction trans;
	private Savepoint sp;
	/**
	 * Store orignal auto commit setting
	 */
	private boolean auto;

	public void invoke(ConnCallback callback) throws Exception {
		// Connection is auto commit, so it must out of transaction
		if (conn.getAutoCommit()) {
			callback.invoke(conn);
		}
		// it have transaction, setup save point
		else {
			if (null == sp)
				sp = conn.setSavepoint();
			callback.invoke(conn);
			// If not transaction, commit current connection
			if (null == trans)
				conn.commit();
		}
	}

	public void rollback() throws SQLException {
		if (null != conn)
			if (null == sp)
				conn.rollback();
			else
				conn.rollback(sp);
	}

	public void close() throws SQLException {
		if (null == trans) {
			conn.setAutoCommit(auto);
			conn.close();
		}
	}

}
