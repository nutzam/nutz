package com.zzh.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import com.zzh.castor.Castors;

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

	static void setBoolean(PreparedStatement stat, Iterator<Integer> it, Boolean v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setBoolean(it.next() + 1, null == v ? false : v);
	}

	static void setNumber(PreparedStatement stat, Iterator<Integer> it, Number v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setObject(it.next() + 1, v);
	}

	static void setChar(PreparedStatement stat, Iterator<Integer> it, Character v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setString(it.next() + 1, v.toString());
	}

	static void setString(PreparedStatement stat, Iterator<Integer> it, Boolean v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setBoolean(it.next() + 1, null == v ? false : v);
	}

	static void setEnumAsChar(PreparedStatement stat, Iterator<Integer> it, Enum<?> v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setString(it.next() + 1, v.name());
	}

	static void setEnumAsInt(PreparedStatement stat, Iterator<Integer> it, Enum<?> v)
			throws SQLException {
		for (; it.hasNext();)
			stat.setInt(it.next() + 1, v.ordinal());
	}

	static void setObject(PreparedStatement stat, Iterator<Integer> it, Object v)
			throws SQLException {
		Object value = Castors.me().castToString(v);
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
