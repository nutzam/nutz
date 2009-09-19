package org.nutz.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.nutz.trans.Transaction;

public class ConnectionHolder {

	public static ConnectionHolder make(Transaction trans, Connection conn) {
		ConnectionHolder ch = new ConnectionHolder();
		ch.trans = trans;
		ch.conn = conn;
		return ch;
	}

	private ConnectionHolder() {}

	private Connection conn;
	private Transaction trans;
	private Savepoint sp;

	public void invoke(ConnCallback callback) throws Exception {
		if (conn.getAutoCommit()) {
			callback.invoke(conn);
		} else {
			sp = conn.setSavepoint();
			callback.invoke(conn);
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
		if (null == trans)
			conn.close();
	}

}
