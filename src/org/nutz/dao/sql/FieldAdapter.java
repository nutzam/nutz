package org.nutz.dao.sql;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import org.nutz.dao.entity.annotation.FieldType;
import org.nutz.lang.Mirror;

public abstract class FieldAdapter {

	private static FieldAdapter AS_NULL = new AsNull();
	private static FieldAdapter AS_STRING = new AsString();
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

	public static FieldAdapter create(Mirror<?> mirror, FieldType.ENUM type) {
		if (null == mirror)
			return AS_NULL;
		if (mirror.isStringLike())
			return AS_STRING;
		if (mirror.isInt())
			return AS_INTEGER;
		if (mirror.isBoolean())
			return AS_BOOLEAN;
		if (mirror.isLong())
			return AS_LONG;
		if (mirror.isEnum()) {
			if (null != type) {
				if (type == FieldType.ENUM.INT)
					return AS_ENUM_INT;
			}
			return AS_ENUM_CHAR;
		}
		if (mirror.isByte())
			return AS_BYTE;
		if (mirror.isShort())
			return AS_SHORT;
		if (mirror.isFloat())
			return AS_FLOAT;
		if (mirror.isDouble())
			return AS_DOUBLE;
		if (mirror.isOf(BigDecimal.class))
			return AS_BIGDECIMAL;
		if (mirror.isOf(Calendar.class))
			return AS_CALENDAR;
		if (mirror.isOf(Timestamp.class))
			return AS_TIMESTAMP;
		if (mirror.isOf(java.util.Date.class))
			return AS_DATE;
		if (mirror.isOf(java.sql.Date.class))
			return AS_SQLDATE;
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
			int v = ((Enum<?>) obj).ordinal();
			for (int i : is)
				stat.setInt(i, v);
		}
	}

	static class AsEnumChar extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			String v = ((Enum<?>) obj).name();
			for (int i : is)
				stat.setString(i, v);
		}
	}

	static class AsObject extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setObject(i, obj);
		}

	}

	static class AsString extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setString(i, obj.toString());
		}
	}

	static class AsInteger extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setInt(i, ((Integer) obj).intValue());
		}
	}

	static class AsBigDecimal extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setBigDecimal(i, (BigDecimal) obj);
		}
	}

	static class AsBoolean extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setBoolean(i, ((Boolean) obj).booleanValue());
		}
	}

	static class AsLong extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setLong(i, ((Long) obj).longValue());
		}
	}

	static class AsByte extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setByte(i, ((Byte) obj).byteValue());
		}
	}

	static class AsShort extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setShort(i, ((Short) obj).shortValue());
		}
	}

	static class AsFloat extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setFloat(i, ((Float) obj).floatValue());
		}
	}

	static class AsDouble extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setDouble(i, ((Double) obj).doubleValue());
		}
	}

	static class AsCalendar extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			Timestamp ts = new Timestamp(((Calendar) obj).getTimeInMillis());
			for (int i : is)
				stat.setTimestamp(i, ts);
		}
	}

	static class AsTimestamp extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setTimestamp(i, (Timestamp) obj);
		}
	}

	static class AsDate extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			Timestamp ts = new Timestamp(((java.util.Date) obj).getTime());
			for (int i : is)
				stat.setTimestamp(i, ts);
		}
	}

	static class AsSqlDate extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setDate(i, ((java.sql.Date) obj));
		}
	}

	static class AsSqlTime extends FieldAdapter {
		public void set(PreparedStatement stat, Object obj, int[] is) throws SQLException {
			for (int i : is)
				stat.setTime(i, ((java.sql.Time) obj));
		}
	}
}
