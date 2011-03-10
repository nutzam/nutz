package org.nutz.el.val;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;

public class BooleanElValue implements ElValue {

	private boolean val;

	public BooleanElValue(boolean val) {
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
		return val;
	}

	public Integer getInteger() {
		return val ? 1 : 0;
	}

	public Float getFloat() {
		return val ? 1f : 0f;
	}

	public Long getLong() {
		return val ? 1L : 0L;
	}

	public String getString() {
		return val ? "true" : "false";
	}

	public ElValue plus(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "plus");
	}

	public ElValue sub(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "sub");
	}

	public ElValue mul(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "mul");
	}

	public ElValue div(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "div");
	}

	public ElValue mod(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "mod");
	}

	public ElValue isEquals(ElValue ta) {
		return val == ta.getBoolean().booleanValue() ? El.TRUE : El.FALSE;
	}

	public ElValue isNEQ(ElValue ta) {
		return val != ta.getBoolean().booleanValue() ? El.TRUE : El.FALSE;
	}

	public ElValue isGT(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "isGT");
	}

	public ElValue isLT(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "isLT");
	}

	public ElValue isGTE(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "isGTE");
	}

	public ElValue isLTE(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "isLTE");
	}

	public String toString() {
		return val ? "true" : "false";
	}

}
