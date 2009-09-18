package org.nutz.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

public class DaoUtils {
	
	public static CharSequence formatFieldValue(Object v) {
		if (null == v)
			return "NULL";
		else if (isNotNeedQuote(v.getClass()))
			return escapeFieldValue(v.toString());
		else
			return new StringBuilder("'").append(escapeFieldValue(v.toString())).append('\'');
	}

	public static CharSequence escapeFieldValue(CharSequence s) {
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

	public static CharSequence escapteConditionValue(CharSequence s) {
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
		return me.isBoolean() || me.isPrimitiveNumber();
	}

	public static ConnectionHolder getConnection(DataSource dataSource) {
		try {
			Transaction trans = Trans.get();
			Connection conn = null;
			if (trans != null)
				conn = trans.getConnection(dataSource);
			else
				conn = dataSource.getConnection();
			return ConnectionHolder.make(trans, conn);
		} catch (SQLException e) {
			throw Lang.makeThrow("Could not get JDBC Connection : %s", e.getMessage());
		}
	}

	public static void releaseConnection(ConnectionHolder ch) {
		try {
			ch.close();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
}
