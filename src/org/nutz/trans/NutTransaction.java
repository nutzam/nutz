package org.nutz.trans;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.nutz.lang.ComboException;

public class NutTransaction extends Transaction {

	private static int ID = 0;

	private List<Pair> list;

	private static class Pair {
		Pair(DataSource ds, Connection conn, int level) throws SQLException {
			this.ds = ds;
			this.conn = conn;
			oldLevel = conn.getTransactionIsolation();
			if (oldLevel != level)
				conn.setTransactionIsolation(level);
		}

		DataSource ds;
		Connection conn;
		int oldLevel;
	}

	public NutTransaction() {
		list = new ArrayList<Pair>();
	}

	@Override
	protected void commit() throws Exception {
		ComboException ce = new ComboException();
		for (Pair p : list) {
			try {
				// 提交事务
				p.conn.commit();
				// 恢复旧的事务级别
				if (p.conn.getTransactionIsolation() != p.oldLevel)
					p.conn.setTransactionIsolation(p.oldLevel);
			} catch (SQLException e) {
				ce.add(e);
			} finally {
				p.conn.close();
			}
		}
		list.clear();
		// 如果有一个数据源提交时发生异常，抛出
		if (null != ce.getCause()) {
			throw ce;
		}
	}

	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		for (Pair p : list)
			if (p.ds == dataSource)
				return p.conn;
		Connection conn = dataSource.getConnection();
		// System.out.printf("=> %s\n", conn.toString());
		if (conn.getAutoCommit())
			conn.setAutoCommit(false);
		// Store conn, it will set the trans level
		list.add(new Pair(dataSource, conn, getLevel()));
		return conn;
	}

	@Override
	public int getId() {
		return ID++;
	}

	@Override
	public void close() throws SQLException {
		for (Pair p : list)
			if (!p.conn.isClosed()) {
				try {
					if (p.conn.getTransactionIsolation() != p.oldLevel)
						p.conn.setTransactionIsolation(p.oldLevel);
				} catch (Exception e) {}
				p.conn.close();
			}
	}

	@Override
	protected void rollback() {
		StringBuilder es = new StringBuilder();
		for (Pair p : list) {
			try {
				p.conn.rollback();
			} catch (Throwable e) {
				es.append(e.getMessage()).append("\n");
			} finally {
				try {
					p.conn.close();
				} catch (SQLException e) {
					es.append(e.getMessage()).append("\n");
				}
			}
		}
		if (es.length() > 0)
			throw new RuntimeException(es.toString());
	}

}
