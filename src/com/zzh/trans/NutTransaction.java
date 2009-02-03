package com.zzh.trans;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

public class NutTransaction extends Transaction {

	private static int ID = 0;

	private Map<DataSource, Connection> map;

	public NutTransaction() {
		map = new HashMap<DataSource, Connection>();
	}

	@Override
	protected void commit()  throws SQLException {
		for (Iterator<Connection> it = map.values().iterator(); it.hasNext();) {
			Connection conn = it.next();
				conn.commit();
				conn.close();
		}
	}

	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		if (map.containsKey(dataSource))
			return map.get(dataSource);
		Connection conn = dataSource.getConnection();
		conn.setAutoCommit(false);
		map.put(dataSource, conn);
		return conn;
	}

	@Override
	public int getId() {
		return ID++;
	}

	@Override
	protected void rollback() {
		StringBuilder es = new StringBuilder();
		for (Iterator<Connection> it = map.values().iterator(); it.hasNext();) {
			Connection conn = it.next();
			try {
				conn.rollback();
				conn.close();
			} catch (Throwable e) {
				es.append(e.toString()).append("\n");
			}
		}
		if (es.length() > 0)
			throw new RuntimeException(es.toString());
	}

}
