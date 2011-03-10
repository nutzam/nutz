package org.nutz.el.val;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;
import org.nutz.lang.Strings;

public class StringElValue extends PojoElValue<String> {

	public StringElValue(String obj) {
		super(obj);
	}

	public Boolean getBoolean() {
		return !Strings.isBlank(obj);
	}

	public Integer getInteger() {
		return Integer.valueOf(obj);
	}

	public Float getFloat() {
		return Float.valueOf(obj);
	}

	public Long getLong() {
		return Long.valueOf(obj);
	}

	public String getString() {
		return obj;
	}

	public ElValue plus(ElValue ta) {
		return new StringElValue(obj + ta.getString());
	}

	public ElValue sub(ElValue ta) {
		Object taObj = ta.get();
		if (taObj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(obj) - (Integer) taObj);
		else if (taObj instanceof Float)
			return new FloatElValue(Float.valueOf(obj) - (Float) taObj);
		else if (taObj instanceof Long)
			return new LongElValue(Long.valueOf(obj) - (Long) taObj);

		throw new ElException(	"String '%s' can not SUB '%s', because it is a '%s'",
								obj,
								taObj,
								taObj.getClass().getSimpleName());
	}

	public ElValue mul(ElValue ta) {
		Object taObj = ta.get();
		if (taObj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(obj) * (Integer) taObj);
		else if (taObj instanceof Float)
			return new FloatElValue(Float.valueOf(obj) * (Float) taObj);
		else if (taObj instanceof Long)
			return new LongElValue(Long.valueOf(obj) * (Long) taObj);

		throw new ElException(	"String '%s' can not MUL '%s', because it is a '%s'",
								obj,
								taObj,
								taObj.getClass().getSimpleName());
	}

	public ElValue div(ElValue ta) {
		Object taObj = ta.get();
		if (taObj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(obj) / (Integer) taObj);
		else if (taObj instanceof Float)
			return new FloatElValue(Float.valueOf(obj) / (Float) taObj);
		else if (taObj instanceof Long)
			return new LongElValue(Long.valueOf(obj) / (Long) taObj);

		throw new ElException(	"String '%s' can not DIV '%s', because it is a '%s'",
								obj,
								taObj,
								taObj.getClass().getSimpleName());
	}

	public ElValue mod(ElValue ta) {
		Object taObj = ta.get();
		if (taObj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(obj) % (Integer) taObj);
		else if (taObj instanceof Float)
			return new FloatElValue(Float.valueOf(obj) % (Float) taObj);
		else if (taObj instanceof Long)
			return new LongElValue(Long.valueOf(obj) % (Long) taObj);

		throw new ElException(	"String '%s' can not MOD '%s', because it is a '%s'",
								obj,
								taObj,
								taObj.getClass().getSimpleName());
	}

	public ElValue isEquals(ElValue ta) {
		return obj.equals(ta.getString()) ? El.TRUE : El.FALSE;
	}

	public ElValue isGT(ElValue ta) {
		return obj.compareTo(ta.getString()) > 0 ? El.TRUE : El.FALSE;
	}

	public ElValue isLT(ElValue ta) {
		return obj.compareTo(ta.getString()) < 0 ? El.TRUE : El.FALSE;
	}

	public ElValue isGTE(ElValue ta) {
		return obj.compareTo(ta.getString()) >= 0 ? El.TRUE : El.FALSE;
	}

	public ElValue isLTE(ElValue ta) {
		return obj.compareTo(ta.getString()) <= 0 ? El.TRUE : El.FALSE;
	}

	public String toString() {
		return "'" + obj + "'";
	}

}
