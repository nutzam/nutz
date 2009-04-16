package com.zzh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class ExecutableSql extends ConditionSql<Object, Object, Connection> {

	public ExecutableSql() {
		super();
	}

	public ExecutableSql(String sql) {
		super(sql);
	}

	// private SqlCallback callback;
	//
	// public void setCallback(SqlCallback next) {
	// this.callback = next;
	// }

	@Override
	public Object execute(Connection conn) throws Exception {
		PreparedStatement stat = null;
		try {
			for (Iterator<String> it = segment.keys().iterator(); it.hasNext();) {
				String key = it.next();
				if (key.startsWith("."))
					segment.set(key, this.get(key));
				else
					segment.set(key, "${" + key + "}");
			}
			valueOf(this.segment.toString());
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
// stat = conn.createStatement();
// for (Iterator<String> it = segment.keys().iterator();
// it.hasNext();) {
// String key = it.next();
// Object obj = this.get(key);
// if (null == obj)
// segment.set(key, key.charAt(0) == '.' ? "" : "NULL");
// else if (key.charAt(0) == '.' ||
// Sqls.isNotNeedQuote(obj.getClass())) {
// segment.set(key, castors.castToString(obj));
// } else {
// segment.set(key, "'" + castors.castToString(obj) +
// "'");
// }
// }
// segment.set("condition", evalCondition());
// stat.execute(this.segment.toString());