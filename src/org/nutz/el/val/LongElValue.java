package org.nutz.el.val;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;

public class LongElValue implements ElValue {

	private Long val;

	public LongElValue(Long val) {
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
		return val.intValue() != 0;
	}

	public Integer getInteger() {
		return val.intValue();
	}

	public Float getFloat() {
		return val.floatValue();
	}

	public Long getLong() {
		return val;
	}

	public String getString() {
		return val.toString();
	}

	public ElValue plus(ElValue ta) {
		return new LongElValue(val + ta.getLong());
	}

	public ElValue sub(ElValue ta) {
		return new LongElValue(val - ta.getLong());
	}

	public ElValue mul(ElValue ta) {
		return new LongElValue(val * ta.getLong());
	}

	public ElValue div(ElValue ta) {
		return new LongElValue(val / ta.getLong());
	}

	public ElValue mod(ElValue ta) {
		return new LongElValue(val % ta.getLong());
	}

	public ElValue isEquals(ElValue ta) {
		return val.equals(ta.getLong()) ? El.TRUE : El.FALSE;
	}

	public ElValue isNEQ(ElValue ta) {
		return !val.equals(ta.getLong()) ? El.TRUE : El.FALSE;
	}

	public ElValue isGT(ElValue ta) {
		return val > ta.getLong() ? El.TRUE : El.FALSE;
	}

	public ElValue isLT(ElValue ta) {
		return val < ta.getLong() ? El.TRUE : El.FALSE;
	}

	public ElValue isGTE(ElValue ta) {
		return val >= ta.getLong() ? El.TRUE : El.FALSE;
	}

	public ElValue isLTE(ElValue ta) {
		return val <= ta.getLong() ? El.TRUE : El.FALSE;
	}

	public String toString() {
		return val.toString() + "L";
	}

}
