package com.zzh.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.trans.Trans;

public class Sqls {

	public static <T> StringBuilder formatFieldValue(T v) {
		if (isNotNeedQuote(v.getClass()))
			return escapeFieldValue(v.toString());
		else
			return new StringBuilder("'").append(escapeFieldValue(v.toString())).append('\'');
	}

	public static StringBuilder escapeFieldValue(CharSequence s) {
		if (null == s)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\'')
				sb.append('\'').append('\'');
			else if (c == '\\')
				sb.append('\\').append('\\');
			else
				sb.append(c);
		}
		return sb;
	}

	public static StringBuilder escapteConditionValue(CharSequence s) {
		if (null == s)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\'')
				sb.append('\'').append('\'');
			else if (c == '\\')
				sb.append('\\').append('\\');
			else if (c == '_')
				sb.append('\\').append(c);
			else if (c == '%')
				sb.append('\\').append(c);
			else
				sb.append(c);
		}
		return sb;
	}

	public static boolean isNotNeedQuote(Class<?> type) {
		Mirror<?> me = Mirror.me(type);
		return !me.isBoolean() && me.isPrimitiveNumber();
	}

	public static Connection getConnection(DataSource dataSource) {
		try {
			if (Trans.get() != null)
				return Trans.get().getConnection(dataSource);
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw Lang.makeThrow("Could not get JDBC Connection : %s", e.getMessage());
		}
	}

	public static void releaseConnection(Connection con, DataSource dataSource) {
		try {
			con.close();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
}
