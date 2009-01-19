package com.zzh.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zzh.dao.callback.Callback;


public class ExecutableSQL<T> extends ConditionSQL<T> {

	public ExecutableSQL() {
		super();
	}

	public ExecutableSQL(String str) {
		valueOf(str);
	}

	public ExecutableSQL(String str, Callback<T> next) {
		valueOf(str);
		setNext(next);
	}

	private Callback<T> next;

	public void setNext(Callback<T> next) {
		this.next = next;
	}

	@Override
	public T execute(Connection conn) throws Exception{
		try {
			Statement stat = conn.createStatement();
			stat.execute(this.toString());
			stat.close();
		} catch (SQLException e) {
			throw new DaoException(this,e);
		}

		if (null != next) {
			setResult(next.invoke(conn));
			return getResult();
		}

		return null;
	}

}
