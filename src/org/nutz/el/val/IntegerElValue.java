package org.nutz.el.val;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;

public class IntegerElValue implements ElValue {

	private Integer val;

	public IntegerElValue(Integer val) {
		this.val = val;
	}

	public ElValue invoke(ElValue[] args) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "invoke");
	}

	public ElValue getProperty(ElValue val) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "getProperty");
	}

	public Object get() {
		return val;
	}

	public Boolean getBoolean() {
		return val != 0;
	}

	public Integer getInteger() {
		return val.intValue();
	}

	public Float getFloat() {
		return val.floatValue();
	}

	public Long getLong() {
		return val.longValue();
	}

	public String getString() {
		return val.toString();
	}

	public ElValue plus(ElValue ta) {
		return new IntegerElValue(val + ta.getInteger());
	}

	public ElValue sub(ElValue ta) {
		return new IntegerElValue(val - ta.getInteger());
	}

	public ElValue mul(ElValue ta) {
		return new IntegerElValue(val * ta.getInteger());
	}

	public ElValue div(ElValue ta) {
		return new IntegerElValue(val / ta.getInteger());
	}

	public ElValue mod(ElValue ta) {
		return new IntegerElValue(val % ta.getInteger());
	}

	public ElValue isEquals(ElValue ta) {
		return val.equals(ta.getInteger()) ? El.TRUE : El.FALSE;
	}

	public ElValue isNEQ(ElValue ta) {
		return !val.equals(ta.getInteger()) ? El.TRUE : El.FALSE;
	}

	public ElValue isGT(ElValue ta) {
		return val > ta.getInteger() ? El.TRUE : El.FALSE;
	}

	public ElValue isLT(ElValue ta) {
		return val < ta.getInteger() ? El.TRUE : El.FALSE;
	}

	public ElValue isGTE(ElValue ta) {
		return val >= ta.getInteger() ? El.TRUE : El.FALSE;
	}

	public ElValue isLTE(ElValue ta) {
		return val <= ta.getInteger() ? El.TRUE : El.FALSE;
	}

	public String toString() {
		return val.toString();
	}

}
