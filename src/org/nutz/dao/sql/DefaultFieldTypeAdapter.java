package org.nutz.dao.sql;

import java.math.BigDecimal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.nutz.castor.Castors;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Lang;

import static org.nutz.dao.sql.FieldTypes.*;

public class DefaultFieldTypeAdapter implements FieldTypeAdapter {

	public void process(PreparedStatement stat, SqlLiteral sql, Entity<?> entity)
			throws SQLException {
		if (null == entity)
			processWithoutEntity(stat, sql);
		else
			processWithEntity(stat, sql, entity);
	}

	private void processWithoutEntity(PreparedStatement stat, SqlLiteral sql) throws SQLException {
		for (String name : sql.getHolders().keys()) {
			Object obj = sql.getHolders().get(name);
			int[] is = sql.getHolderIndexes(name);
			if (null == is || is.length == 0)
				continue;
			// NULL
			if (null == obj) {
				asNull(stat, is);
			}
			// String
			else if (obj instanceof CharSequence) {
				asString(stat, obj, is);
			}
			// Integer
			else if (obj instanceof Integer) {
				asInteger(stat, obj, is);
			}
			// Boolean
			else if (obj instanceof Boolean) {
				asBoolean(stat, obj, is);
			}
			// Timestamp
			else if (obj instanceof Timestamp) {
				asTimestamp(stat, obj, is);
			}
			// Long
			else if (obj instanceof Long) {
				asLong(stat, obj, is);
			}
			// Enum
			else if (obj.getClass().isEnum()) {
				asString(stat, obj, is);
			}
			// Float
			else if (obj instanceof Float) {
				asFloat(stat, obj, is);
			}
			// BigDecimal
			else if (obj instanceof BigDecimal) {
				asBigDecimal(stat, obj, is);
			}
			// Byte
			else if (obj instanceof Byte) {
				asByte(stat, obj, is);
			}
			// Short
			else if (obj instanceof Short) {
				asShort(stat, obj, is);
			}
			// Double
			else if (obj instanceof Double) {
				asDouble(stat, obj, is);
			}
			// Calendar
			else if (obj instanceof Calendar) {
				asCalendar(stat, obj, is);
			}
			// Date
			else if (obj instanceof java.util.Date) {
				asDate(stat, obj, is);
			}
			// SqlDate
			else if (obj instanceof java.sql.Date) {
				asSqlDate(stat, obj, is);
			}
			// SqlTime
			else if (obj instanceof java.sql.Time) {
				asSqlTime(stat, obj, is);
			}
			// Default: all object as string
			else {
				asString(stat, Castors.me().castToString(obj), is);
			}
		}
	}

	private void processWithEntity(PreparedStatement stat, SqlLiteral sql, Entity<?> entity)
			throws SQLException {
		for (EntityField ef : entity.fields()) {
			String name = ef.getField().getName();
			Object obj = sql.getHolders().get(name);
			int[] is = sql.getHolderIndexes(name);
			if (ef == null)
				continue;
			// NULL
			if (null == obj) {
				if (null != ef)
					if (ef.isNotNull())
						throw Lang.makeThrow("Field %s(%s).%s(%s) can not be NULL.", entity
								.getType().getName(), entity.getTableName(), ef.getField()
								.getName(), ef.getColumnName());
				asNull(stat, is);
			}
			// String
			else if (ef.getField().getType().isAssignableFrom(CharSequence.class)) {
				asString(stat, obj, is);
			}
			// Integer
			else if (ef.getField().getType().isAssignableFrom(Integer.class)) {
				asInteger(stat, obj, is);
			}
			// Boolean
			else if (ef.getField().getType().isAssignableFrom(Boolean.class)) {
				asBoolean(stat, obj, is);
			}
			// Timestamp
			else if (ef.getField().getType().isAssignableFrom(Timestamp.class)) {
				asTimestamp(stat, obj, is);
			}
			// Long
			else if (ef.getField().getType().isAssignableFrom(Long.class)) {
				asLong(stat, obj, is);
			}
			// Enum
			else if (ef.getField().getType().isEnum()) {
				asString(stat, obj, is);
			}
			// Float
			else if (ef.getField().getType().isAssignableFrom(Float.class)) {
				asFloat(stat, obj, is);
			}
			// BigDecimal
			else if (ef.getField().getType().isAssignableFrom(BigDecimal.class)) {
				asBigDecimal(stat, obj, is);
			}
			// Byte
			else if (ef.getField().getType().isAssignableFrom(Byte.class)) {
				asByte(stat, obj, is);
			}
			// Short
			else if (ef.getField().getType().isAssignableFrom(Short.class)) {
				asShort(stat, obj, is);
			}
			// Double
			else if (ef.getField().getType().isAssignableFrom(Double.class)) {
				asDouble(stat, obj, is);
			}
			// Calendar
			else if (ef.getField().getType().isAssignableFrom(Calendar.class)) {
				asCalendar(stat, obj, is);
			}
			// Date
			else if (ef.getField().getType().isAssignableFrom(java.util.Date.class)) {
				asDate(stat, obj, is);
			}
			// SqlDate
			else if (ef.getField().getType().isAssignableFrom(java.sql.Date.class)) {
				asSqlDate(stat, obj, is);
			}
			// SqlTime
			else if (ef.getField().getType().isAssignableFrom(java.sql.Time.class)) {
				asSqlTime(stat, obj, is);
			}
			// Default: all object as string
			else {
				asString(stat, Castors.me().castToString(obj), is);
			}
		}

	}

}
