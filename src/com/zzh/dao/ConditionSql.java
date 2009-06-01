package com.zzh.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.dao.Condition;
import com.zzh.dao.callback.Callback;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;
import com.zzh.lang.segment.Segment;

import static java.lang.String.*;

/**
 * Preserver plug name "condition"
 * 
 * @author zozoh
 * 
 */
public abstract class ConditionSql<T, R, P> extends AbstractSql<T> {

	public ConditionSql() {
		super();
	}

	public ConditionSql(String sql) {
		super(sql);
	}

	protected Callback<R, P> callback;

	public ConditionSql<T, R, P> setCallback(Callback<R, P> callback) {
		this.callback = callback;
		return this;
	}

	private T result;

	private Condition condition;

	@Override
	public T getResult() {
		return result;
	}

	public void setResult(T object) {
		this.result = object;
	}

	protected PreparedStatement setupStatement(PreparedStatement stat)
			throws FailToCastObjectException {
		Entity<?> entity = this.getEntity();
		for (Iterator<String> it = segment.keys().iterator(); it.hasNext();) {
			String key = it.next();
			EntityField ef = (null == entity ? null : entity.getField(key));
			if ("condition".equals(key))
				continue;
			Object value = values.get(key);
			Mirror<?> mirror = Mirror.me(ef == null ? null == value ? null : value.getClass() : ef
					.getField().getType());
			try {
				List<Integer> indexes = this.segment.getIndex(key);
				if (null == indexes || indexes.size() == 0)
					continue;
				if (null == value) {
					Pss.setNull(stat, indexes.iterator(), java.sql.Types.VARCHAR);
				} else if (null != mirror && mirror.isBoolean()) {
					Pss.setBoolean(stat, indexes.iterator(), (Boolean) value);
				} else if (null != mirror && mirror.isChar()) {
					Pss.setChar(stat, indexes.iterator(), (Character) value);
				} else if (null != mirror && mirror.isNumber()) {
					Pss.setNumber(stat, indexes.iterator(), (Number) value);
				} else if (null == value) {
					Pss.setNull(stat, indexes.iterator(), java.sql.Types.VARCHAR);
				} else if (value instanceof Timestamp)
					Pss.setTimestamp(stat, indexes.iterator(), (Timestamp) value);
				else if (value instanceof Calendar) {
					Timestamp v = Castors.me().cast(value, Calendar.class, Timestamp.class);
					Pss.setTimestamp(stat, indexes.iterator(), v);
				} else if (value instanceof java.util.Date) {
					Timestamp v = Castors.me().cast(value, java.util.Date.class, Timestamp.class);
					Pss.setTimestamp(stat, indexes.iterator(), v);
				} else if (value instanceof java.sql.Date) {
					Pss.setSqlDate(stat, indexes.iterator(), (java.sql.Date) value);
				} else if (value instanceof java.sql.Time) {
					Pss.setSqlTime(stat, indexes.iterator(), (Time) value);
				} else if (null != mirror && mirror.isOf(Enum.class)) {
					if (ef != null && ef.isInt())
						Pss.setEnumAsInt(stat, indexes.iterator(), (Enum<?>) value);
					else
						Pss.setEnumAsChar(stat, indexes.iterator(), (Enum<?>) value);
				} else {
					Pss.setObject(stat, indexes.iterator(), value);
				}
			} catch (SQLException e) {
				throw new DaoException(String.format(
						"Fail to prepareStatement: %s, for the reason '%s'", key, e.getMessage()));
			}
		}
		return stat;
	}

	public ConditionSql<T, R, P> setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	private static final Pattern ptn = Pattern.compile("^(WHERE |ORDER BY )",
			Pattern.CASE_INSENSITIVE);;

	protected Object evalCondition() {
		if (null != condition) {
			String cond = Strings.trim(condition.toString(getEntity()));
			if (Strings.isEmpty(cond))
				return "";
			if (!ptn.matcher(cond).find()) {
				return " WHERE " + cond;
			}
			return " " + cond;
		}
		return "";
	}

	@Override
	public String toString() {
		Segment seg = segment.clone();
		for (Iterator<String> it = seg.keys().iterator(); it.hasNext();) {
			String key = it.next();
			Object obj = this.get(key);
			if (null == obj)
				seg.set(key, key.charAt(0) == '.' ? "" : "NULL");
			else {
				Mirror<?> mirror = Mirror.me(obj.getClass());
				if (mirror.isEnum() && null != this.getEntity()) {
					EntityField ef = getEntity().getField(key);
					if (ef.isInt()) {
						seg.set(key, ((Enum<?>) obj).ordinal());
					} else {
						seg.set(key, "'" + ((Enum<?>) obj).name() + "'");
					}
				} else if (mirror.is(Timestamp.class)) {
					seg.set(key, format("'%s'", obj.toString()));
				} else if (mirror.isChar()) {
					seg.set(key, format("'%s'", (0 == (Character) obj ? ' ' : (Character) obj)));
				} else if (key.charAt(0) == '.' || Sqls.isNotNeedQuote(obj.getClass())) {
					seg.set(key, Castors.me().castToString(obj));
				} else {
					seg.set(key, format("'%s'", Sqls.escapeFieldValue(Castors.me()
							.castToString(obj))));
				}
			}
		}
		seg.set("condition", evalCondition());
		return seg.toString();
	}

	public String getPreparedStatementString() {
		for (Iterator<String> it = segment.keys().iterator(); it.hasNext();) {
			String key = it.next();
			if (key.startsWith("."))
				segment.set(key, this.get(key));
			else
				segment.set(key, "${" + key + "}");
		}
		valueOf(this.segment.toString());
		return this.getSegment().setAll('?').set("condition", evalCondition()).toString();
	}

}
