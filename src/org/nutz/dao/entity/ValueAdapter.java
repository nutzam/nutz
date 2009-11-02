package org.nutz.dao.entity;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.nutz.lang.Mirror;

/**
 * 将 ResultSet 的值读出来
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class ValueAdapter {

	private static ValueAdapter AS_STRING = new AsString();
	private static ValueAdapter AS_INTEGER = new AsInteger();
	private static ValueAdapter AS_BIGDECIMAL = new AsBigDecimal();
	private static ValueAdapter AS_BOOLEAN = new AsBoolean();
	private static ValueAdapter AS_LONG = new AsLong();
	private static ValueAdapter AS_BYTE = new AsByte();
	private static ValueAdapter AS_SHORT = new AsShort();
	private static ValueAdapter AS_FLOAT = new AsFloat();
	private static ValueAdapter AS_DOUBLE = new AsDouble();
	private static ValueAdapter AS_CALENDAR = new AsCalendar();
	private static ValueAdapter AS_TIMESTAMP = new AsTimestamp();
	private static ValueAdapter AS_DATE = new AsDate();
	private static ValueAdapter AS_SQLDATE = new AsSqlDate();
	private static ValueAdapter AS_SQLTIME = new AsSqlTime();

	public static ValueAdapter create(Mirror<?> mirror, FieldValueType type) {
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
			if (null != type) {
				if (type == FieldValueType.INT)
					return AS_INTEGER;
			}
			return AS_STRING;
		}
		// Char
		if (mirror.isChar())
			return AS_STRING;
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
		return AS_STRING;
	}

	public abstract Object get(ResultSet rs, String colnm) throws SQLException;

	/* ====================================================================== */
	private static class AsString extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getString(colnm);
		}
	}

	private static class AsInteger extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getInt(colnm);
		}
	}

	private static class AsBigDecimal extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getBigDecimal(colnm);
		}
	}

	private static class AsBoolean extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getBoolean(colnm);
		}
	}

	private static class AsLong extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getLong(colnm);
		}
	}

	private static class AsByte extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getByte(colnm);
		}
	}

	private static class AsShort extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getShort(colnm);
		}
	}

	private static class AsFloat extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getFloat(colnm);
		}
	}

	private static class AsDouble extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getDouble(colnm);
		}
	}

	private static class AsCalendar extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			Timestamp ts = rs.getTimestamp(colnm);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(ts.getTime());
			return c;
		}
	}

	private static class AsTimestamp extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getTimestamp(colnm);
		}
	}

	private static class AsDate extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			Timestamp ts = rs.getTimestamp(colnm);
			return new java.util.Date(ts.getTime());
		}
	}

	private static class AsSqlDate extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getDate(colnm);
		}
	}

	private static class AsSqlTime extends ValueAdapter {
		public Object get(ResultSet rs, String colnm) throws SQLException {
			return rs.getTime(colnm);
		}
	}

}
