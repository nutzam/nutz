package com.zzh.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.trans.Trans;

public class SQLUtils {

	public static SQL<?> create(String sql) {
		return null;
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

	public static StringBuilder escapteCondition(CharSequence s) {
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

	@Deprecated
	public static String formatName(CharSequence cs) {
		String name = null;
		if (cs.charAt(0) == '`' || cs.charAt(cs.length() - 1) == '`') {
			if (cs.charAt(0) == '`' && cs.charAt(cs.length() - 1) == '`') {
				char[] ca = new char[cs.length() - 2];
				for (int i = 0; i < ca.length; i++)
					ca[i] = cs.charAt(i + 1);
				name = String.valueOf(ca);
			} else
				throw new RuntimeException("Error field name: \"" + cs.toString() + "\"");
		} else
			name = new StringBuilder("`").append(cs).append('`').toString();
		if (null == name || name.length() == 0) {
			throw new RuntimeException("Error field name: \"" + cs.toString() + "\"");
		}
		return name;
	}

	public static SQLException wrapSQLException(Throwable t) {
		if (t instanceof SQLException)
			return (SQLException) t;
		return new SQLException(t.getMessage());
	}

	public static Connection getConnection(DataSource dataSource) {
		try {
			if (Trans.get() != null)
				return Trans.get().getConnection(dataSource);
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get JDBC Connection", e);
		}
	}

	public static void releaseConnection(Connection con, DataSource dataSource) {
		try {
			if (Trans.get() == null)
				con.close();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
}
