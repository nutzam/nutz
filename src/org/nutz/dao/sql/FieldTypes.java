package org.nutz.dao.sql;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

public class FieldTypes {
	public static void asNull(PreparedStatement stat, int[] is) throws SQLException {
		for (int i : is)
			stat.setNull(i + 1, Types.NULL);
	}

	public static void asString(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setString(i + 1, obj.toString());
	}

	public static void asInteger(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setInt(i + 1, ((Integer) obj).intValue());
	}

	public static void asBigDecimal(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setBigDecimal(i + 1, (BigDecimal) obj);
	}

	public static void asBoolean(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setBoolean(i + 1, ((Boolean) obj).booleanValue());
	}

	public static void asLong(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setLong(i + 1, ((Long) obj).longValue());
	}

	public static void asByte(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setByte(i + 1, ((Byte) obj).byteValue());
	}

	public static void asShort(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setShort(i + 1, ((Short) obj).shortValue());
	}

	public static void asFloat(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setFloat(i + 1, ((Float) obj).floatValue());
	}

	public static void asDouble(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setDouble(i + 1, ((Double) obj).doubleValue());
	}

	public static void asCalendar(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		Timestamp ts = new Timestamp(((Calendar) obj).getTimeInMillis());
		for (int i : is)
			stat.setTimestamp(i + 1, ts);
	}

	public static void asTimestamp(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setTimestamp(i + 1, (Timestamp) obj);
	}

	public static void asDate(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		Timestamp ts = new Timestamp(((java.util.Date) obj).getTime());
		for (int i : is)
			stat.setTimestamp(i + 1, ts);
	}

	public static void asSqlDate(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setDate(i + 1, ((Date) obj));
	}

	public static void asSqlTime(PreparedStatement stat, Object obj, int[] is) throws SQLException {
		for (int i : is)
			stat.setTime(i + 1, ((Time) obj));
	}

}
