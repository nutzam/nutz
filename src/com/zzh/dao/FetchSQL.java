package com.zzh.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zzh.dao.callback.QueryCallback;

public class FetchSQL<T> extends ConditionSQL<T> {

	public FetchSQL() {
		super();
	}

	public FetchSQL(String str) {
		valueOf(str);
	}

	public FetchSQL(String str, QueryCallback<T> callback) {
		valueOf(str);
		setCallback(callback);
	}

	private QueryCallback<T> queryCallback;

	public void setCallback(QueryCallback<T> callback) {
		this.queryCallback = callback;
	}

	public QueryCallback<T> getCallback() {
		return queryCallback;
	}

	@Override
	public T execute(Connection conn) throws Exception {
		setResult(execute(conn, queryCallback));
		return getResult();
	}

	protected T execute(Connection conn, QueryCallback<T> callback) throws SQLException {
		Statement stat = null;
		try {
			T o = null;
			stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stat.executeQuery(this.toString());
			if (rs.first())
				o = callback.invoke(rs);
			rs.close();
			return o;
		} catch (SQLException e) {
			throw new DaoException(this, e);
		} finally {
			if (null != stat)
				try {
					stat.close();
				} catch (SQLException e1) {
				}
		}
	}

}
