package org.nutz.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ExecutableSql extends ConditionSql<Object, Object, Connection> {

	public ExecutableSql() {
		super();
	}

	public ExecutableSql(String sql) {
		super(sql);
	}

	@Override
	public Object execute(Connection conn) throws Exception {
		Statement stat = null;
		try {
			// stat = conn.prepareStatement(this.getPreparedStatementString());
			// super.setupStatement(stat);
			// stat.execute();
			stat = conn.createStatement();
			String sql = this.toString();
			executeSql(stat, sql);
		} catch (SQLException e) {
			throw new DaoException(this, e);
		} finally {
			if (null != stat)
				try {
					stat.close();
				} catch (SQLException e1) {}
		}
		if (null != callback) {
			setResult(callback.invoke(conn));
			return getResult();
		}
		return null;
	}

	private void executeSql(Statement stat, String sql) throws SQLException {
		stat.execute(sql);
	}

}