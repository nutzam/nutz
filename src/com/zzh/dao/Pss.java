package com.zzh.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import com.zzh.castor.Castors;
import com.zzh.lang.Mirror;

class Pss {

	static void setTimestamp(PreparedStatement stat, Iterator<Integer> it, java.sql.Timestamp v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setTimestamp(it.next() + 1, v);
	}

	static void setSqlDate(PreparedStatement stat, Iterator<Integer> it, java.sql.Date v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setDate(it.next() + 1, v);
	}

	static void setSqlTime(PreparedStatement stat, Iterator<Integer> it, java.sql.Time v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setTime(it.next() + 1, v);
	}

	static void setBoolean(PreparedStatement stat, Iterator<Integer> it, boolean v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setBoolean(it.next() + 1, v);
	}

	static void setObject(PreparedStatement stat, Iterator<Integer> it, Object v, Castors castors)
			throws SQLException {
		Mirror<?> mirror = Mirror.me(v.getClass());
		Object value = mirror.isOf(java.io.Serializable.class) ? (mirror.isStringLike() ? v
				.toString() : v) : castors.castToString(v);
		for (; it.hasNext();) {
			Integer i = it.next();
			stat.setObject(i + 1, value);
		}
	}

	static void setNull(PreparedStatement stat, Iterator<Integer> it, int sqlType)
			throws SQLException {
		for (; it.hasNext();)
			stat.setNull(it.next() + 1, sqlType);
	}
}
