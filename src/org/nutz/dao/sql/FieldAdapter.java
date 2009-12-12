package org.nutz.dao.sql;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;

/**
 * 将值某一字段的值设置到 PreparedStatement 中
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class FieldAdapter {

	public final static FieldAdapter AS_NULL = new AsNull();
	private static FieldAdapter AS_STRING = new AsString();
	private static FieldAdapter AS_CHAR = new AsChar();
	private static FieldAdapter AS_INTEGER = new AsInteger();
	private static FieldAdapter AS_BIGDECIMAL = new AsBigDecimal();
	private static FieldAdapter AS_BOOLEAN = new AsBoolean();
	private static FieldAdapter AS_LONG = new AsLong();
	private static FieldAdapter AS_BYTE = new AsByte();
	private static FieldAdapter AS_SHORT = new AsShort();
	private static FieldAdapter AS_FLOAT = new AsFloat();
	private static FieldAdapter AS_DOUBLE = new AsDouble();
	private static FieldAdapter AS_CALENDAR = new AsCalendar();
	private static FieldAdapter AS_TIMESTAMP = new AsTimestamp();
	private static FieldAdapter AS_DATE = new AsDate();
	private static FieldAdapter AS_SQLDATE = new AsSqlDate();
	private static FieldAdapter AS_SQLTIME = new AsSqlTime();
	private static FieldAdapter AS_ENUM_INT = new AsEnumInt();
	private static FieldAdapter AS_ENUM_CHAR = new AsEnumChar();
	private static FieldAdapter AS_OBJECT = new AsObject();

	public static FieldAdapter create(Mirror<?> mirror, boolean isEnumInt) {
		// NULL
		if (null == mirror)
			return AS_NULL;
		// String and char
		if (mirror.isStringLike())
			return AS_STRING;
		// Int
		if (mirror.isInt())
			return AS_INTEGER;
		// Boolean
		if (mirror.isBoolean())
			return AS_BOOLEAN;
		// Long
		if (mirror.isLong())
			return AS_LONG;
		// Enum
		if (mirror.isEnum()) {
			if (isEnumInt)
				return AS_ENUM_INT;
			return AS_ENUM_CHAR;
		}
		// Char
		if (mirror.isChar())
			return AS_CHAR;
		// Timestamp
		if (mirror.isOf(Timestamp.class))
			return AS_TIMESTAMP;
		// Byte
		if (mirror.isByte())
			return AS_BYTE;
		// Short
		if (mirror.isShort())
			return AS_SHORT;
		// Float
		if (mirror.isFloat())
			return AS_FLOAT;
		// Double
		if (mirror.isDouble())
			return AS_DOUBLE;
		// BigDecimal
		if (mirror.isOf(BigDecimal.class))
			return AS_BIGDECIMAL;
		// Calendar
		if (mirror.isOf(Calendar.class))
			return AS_CALENDAR;
		// java.util.Date
		if (mirror.isOf(java.util.Date.class))
			return AS_DATE;
		// java.sql.Date
		if (mirror.isOf(java.sql.Date.class))
			return AS_SQLDATE;
		// java.sql.Time
		if (mirror.isOf(java.sql.Time.class))
			return AS_SQLTIME;
		return AS_OBJECT;
	}

	public abstract void set(PreparedStatement stat, Object obj, int[] is) throws SQLException;

	static class AsNull extends FieldAdapter {

		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setNull(i, Types.NULL);
		}

	}

	static class AsEnumInt extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.INTEGER);
			} else {
				int v;
				if (obj instanceof Enum<?>)
					v = ((Enum<?>) obj).ordinal();
				else
					v = Castors.me().castTo(obj, int.class);
				for (int i : is)
					stat.setInt(i, v);
			}
		}
	}

	private static class AsEnumChar extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setString(i, null);
			} else {
				String v = obj.toString();
				for (int i : is)
					stat.setString(i, v);
			}
		}
	}

	private static class AsObject extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setString(i, null);
			} else {
				String v = Castors.me().castToString(obj);
				for (int i : is)
					stat.setString(i, v);
			}
		}

	}

	private static class AsString extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setString(i, null);
			} else {
				for (int i : is)
					stat.setString(i, obj.toString());
			}
		}
	}

	private static class AsChar extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setString(i, null);
			} else {
				String s;
				if (obj instanceof Character) {
					int c = ((Character) obj).charValue();
					if (c >= 0 && c <= 32)
						s = " ";
					else
						s = String.valueOf((char) c);
				} else
					s = obj.toString();
				for (int i : is)
					stat.setString(i, s);
			}
		}
	}

	private static class AsInteger extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.INTEGER);
			} else {
				int v;
				if (obj instanceof Number)
					v = ((Number) obj).intValue();
				else
					v = Castors.me().castTo(obj.toString(), int.class);
				for (int i : is)
					stat.setInt(i, v);
			}
		}
	}

	private static class AsBigDecimal extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.BIGINT);
			} else {
				BigDecimal v;
				if (obj instanceof BigDecimal)
					v = (BigDecimal) obj;
				else if (obj instanceof Number)
					v = BigDecimal.valueOf(((Number) obj).longValue());
				else
					v = new BigDecimal(obj.toString());
				for (int i : is)
					stat.setBigDecimal(i, v);
			}
		}
	}

	private static class AsBoolean extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				/*
				 * 对 Oracle，Types.BOOLEAN 对于 setNull 是不工作的
				 * 其他的数据库都没有这个问题，所以，只好把类型设成 INTEGER了
				 */
				for (int i : is)
					stat.setNull(i, Types.INTEGER);
			} else {
				boolean v;
				if (obj instanceof Boolean)
					v = (Boolean) obj;
				else if (obj instanceof Number)
					v = ((Number) obj).intValue() > 0;
				else if (obj instanceof Character)
					v = Character.toUpperCase((Character) obj) == 'T';
				else
					v = Boolean.valueOf(obj.toString());
				for (int i : is)
					stat.setBoolean(i, v);
			}
		}
	}

	private static class AsLong extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.INTEGER);
			} else {
				long v;
				if (obj instanceof Number)
					v = ((Number) obj).longValue();
				else
					v = Castors.me().castTo(obj.toString(), long.class);
				for (int i : is)
					stat.setLong(i, v);
			}
		}
	}

	private static class AsByte extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.TINYINT);
			} else {
				byte v;
				if (obj instanceof Number)
					v = ((Number) obj).byteValue();
				else
					v = Castors.me().castTo(obj.toString(), byte.class);
				for (int i : is)
					stat.setByte(i, v);
			}
		}
	}

	private static class AsShort extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.SMALLINT);
			} else {
				short v;
				if (obj instanceof Number)
					v = ((Number) obj).shortValue();
				else
					v = Castors.me().castTo(obj.toString(), short.class);
				for (int i : is)
					stat.setShort(i, v);
			}
		}
	}

	private static class AsFloat extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.FLOAT);
			} else {
				float v;
				if (obj instanceof Number)
					v = ((Number) obj).floatValue();
				else
					v = Castors.me().castTo(obj.toString(), float.class);
				for (int i : is)
					stat.setFloat(i, v);
			}
		}
	}

	private static class AsDouble extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.DOUBLE);
			} else {
				double v;
				if (obj instanceof Number)
					v = ((Number) obj).doubleValue();
				else
					v = Castors.me().castTo(obj.toString(), double.class);
				for (int i : is)
					stat.setDouble(i, v);
			}
		}
	}

	private static class AsCalendar extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.TIMESTAMP);
			} else {
				Timestamp v;
				if (obj instanceof Calendar)
					v = new Timestamp(((Calendar) obj).getTimeInMillis());
				else
					v = Castors.me().castTo(obj, Timestamp.class);
				for (int i : is)
					stat.setTimestamp(i, v);
			}
		}
	}

	private static class AsTimestamp extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.TIMESTAMP);
			} else {
				Timestamp v;
				if (obj instanceof Timestamp)
					v = (Timestamp) obj;
				else
					v = Castors.me().castTo(obj, Timestamp.class);
				for (int i : is)
					stat.setTimestamp(i, v);
			}
		}
	}

	private static class AsDate extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			Timestamp v;
			if (obj instanceof java.util.Date)
				v = new Timestamp(((java.util.Date) obj).getTime());
			else
				v = Castors.me().castTo(obj, Timestamp.class);
			for (int i : is)
				stat.setTimestamp(i, v);
		}
	}

	private static class AsSqlDate extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.DATE);
			} else {
				java.sql.Date v;
				if (obj instanceof java.sql.Date)
					v = (java.sql.Date) obj;
				else
					v = Castors.me().castTo(obj, java.sql.Date.class);
				for (int i : is)
					stat.setDate(i, v);
			}
		}
	}

	private static class AsSqlTime extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			java.sql.Time v;
			if (null == obj) {
				for (int i : is)
					stat.setNull(i, Types.TIME);
			} else {
				if (obj instanceof java.sql.Time)
					v = (java.sql.Time) obj;
				else
					v = Castors.me().castTo(obj, java.sql.Time.class);
				for (int i : is)
					stat.setTime(i, v);
			}
		}
	}

}
