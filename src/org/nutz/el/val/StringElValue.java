package org.nutz.el.val;

import org.nutz.el.ElException;
import org.nutz.el.ElValue;
import org.nutz.lang.Strings;

public class StringElValue implements ElValue {

	private String str;

	public StringElValue(String str) {
		this.str = str;
	}

	public ElValue invoke(ElValue[] args) {
		String name = args[0].getString();
		if (name.equals("trim")) {
			return new StringElValue(Strings.trim(str));
		}
		throw new ElException("%s has not method [%s]!", getClass().getSimpleName(), name);
	}

	public ElValue getProperty(ElValue val) {
		String name = val.getString();
		if (name.equals("length"))
			return new IntegerElValue(str.length());
		throw new ElException("%s has not property [%s]!", getClass().getSimpleName(), name);
	}

	public Object get() {
		return str;
	}

	public Boolean getBoolean() {
		return !Strings.isBlank(str);
	}

	public Integer getInteger() {
		return Integer.valueOf(str);
	}

	public Float getFloat() {
		return Float.valueOf(str);
	}

	public Long getLong() {
		return Long.valueOf(str);
	}

	public String getString() {
		return str;
	}

	public ElValue plus(ElValue ta) {
		return new StringElValue(str + ta.getString());
	}

	public ElValue sub(ElValue ta) {
		Object obj = ta.get();
		if (obj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(str) - (Integer) obj);
		else if (obj instanceof Float)
			return new FloatElValue(Float.valueOf(str) - (Float) obj);
		else if (obj instanceof Long)
			return new LongElValue(Long.valueOf(str) - (Long) obj);

		throw new ElException(	"String '%s' can not SUB '%s', because it is a '%s'",
								str,
								obj,
								obj.getClass().getSimpleName());
	}

	public ElValue mul(ElValue ta) {
		Object obj = ta.get();
		if (obj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(str) * (Integer) obj);
		else if (obj instanceof Float)
			return new FloatElValue(Float.valueOf(str) * (Float) obj);
		else if (obj instanceof Long)
			return new LongElValue(Long.valueOf(str) * (Long) obj);

		throw new ElException(	"String '%s' can not MUL '%s', because it is a '%s'",
								str,
								obj,
								obj.getClass().getSimpleName());
	}

	public ElValue div(ElValue ta) {
		Object obj = ta.get();
		if (obj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(str) / (Integer) obj);
		else if (obj instanceof Float)
			return new FloatElValue(Float.valueOf(str) / (Float) obj);
		else if (obj instanceof Long)
			return new LongElValue(Long.valueOf(str) / (Long) obj);

		throw new ElException(	"String '%s' can not DIV '%s', because it is a '%s'",
								str,
								obj,
								obj.getClass().getSimpleName());
	}

	public ElValue mod(ElValue ta) {
		Object obj = ta.get();
		if (obj instanceof Integer)
			return new IntegerElValue(Integer.valueOf(str) % (Integer) obj);
		else if (obj instanceof Float)
			return new FloatElValue(Float.valueOf(str) % (Float) obj);
		else if (obj instanceof Long)
			return new LongElValue(Long.valueOf(str) % (Long) obj);

		throw new ElException(	"String '%s' can not MOD '%s', because it is a '%s'",
								str,
								obj,
								obj.getClass().getSimpleName());
	}

	public ElValue isEquals(ElValue ta) {
		return new BooleanElValue(str.equals(ta.getString()));
	}

	public ElValue isGT(ElValue ta) {
		return new BooleanElValue(str.compareTo(ta.getString()) > 0);
	}

	public ElValue isLT(ElValue ta) {
		return new BooleanElValue(str.compareTo(ta.getString()) < 0);
	}

	public ElValue isGTE(ElValue ta) {
		return new BooleanElValue(str.compareTo(ta.getString()) >= 0);
	}

	public ElValue isLTE(ElValue ta) {
		return new BooleanElValue(str.compareTo(ta.getString()) <= 0);
	}

	public String toString() {
		return "'" + str + "'";
	}

}
