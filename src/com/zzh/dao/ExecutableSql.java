package com.zzh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExecutableSql extends ConditionSql<Object, Object, Connection> {

	public ExecutableSql() {
		super();
	}

	public ExecutableSql(String sql) {
		super(sql);
	}

	@Override
	public Object execute(Connection conn) throws Exception {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(this.getPreparedStatementString());
			super.setupStatement(stat);
			stat.execute();
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

}