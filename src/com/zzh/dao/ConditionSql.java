package com.zzh.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.dao.Condition;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

/**
 * Preserver plug name "condition"
 * 
 * @author zozoh
 * 
 */
public abstract class ConditionSql<T> extends AbstractSql<T> {

	protected ConditionSql(Castors castors) {
		super(castors);
	}

	private T result;

	private Condition condition;

	public T getResult() {
		return result;
	}

	public void setResult(T object) {
		this.result = object;
	}

	protected PreparedStatement setupStatement(PreparedStatement stat)
			throws FailToCastObjectException {
		for (Iterator<String> it = segment.keys().iterator(); it.hasNext();) {
			String key = it.next();
			if ("condition".equals(key))
				continue;
			Object value = values.get(key);
			try {
				List<Integer> indexes = this.segment.getIndex(key);
				if (null == indexes || indexes.size() == 0)
					continue;
				if (null == value) {
					Pss.setNull(stat, indexes.iterator(), java.sql.Types.VARCHAR);
				} else if (value instanceof Timestamp)
					Pss.setTimestamp(stat, indexes.iterator(), (Timestamp) value);
				else if (value instanceof Calendar) {
					Timestamp v = castors.cast(value, Calendar.class, Timestamp.class);
					Pss.setTimestamp(stat, indexes.iterator(), v);
				} else if (value instanceof java.util.Date) {
					Timestamp v = castors.cast(value, java.util.Date.class, Timestamp.class);
					Pss.setTimestamp(stat, indexes.iterator(), v);
				} else if (value instanceof java.sql.Date) {
					Pss.setSqlDate(stat, indexes.iterator(), (java.sql.Date) value);
				} else if (value instanceof java.sql.Time) {
					Pss.setSqlTime(stat, indexes.iterator(), (Time) value);
				} else if (Mirror.me(value.getClass()).isBoolean()) {
					Pss.setBoolean(stat, indexes.iterator(), (Boolean) value);
				} else {
					Pss.setObject(stat, indexes.iterator(), value, castors);
				}
			} catch (SQLException e) {
				throw new DaoException(String.format("Fail to prepareStatement: %s", key));
			}
		}
		return stat;
	}

	public ConditionSql<T> setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	protected Object evalCondition() {
		if (null != condition) {
			String cond = Strings.trim(condition.toString(getEntity()));
			if (Strings.isEmpty(cond))
				return "";
			if (!cond.toUpperCase().startsWith("WHERE")) {
				cond = "WHERE " + cond;
			}
			return " " + cond;
		}
		return "";
	}

	@Override
	public String toString() {
		return this.getSegment().toString();
	}

	public String getPreparedStatementString() {
		return this.getSegment().setAll('?').set("condition", evalCondition()).toString();
	}

}
