package org.nutz.el.val;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;

public class NullElValue implements ElValue {

	public ElValue invoke(ElValue[] args) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "invoke");
	}

	public ElValue getProperty(ElValue val) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "getProperty");
	}

	public Object get() {
		return null;
	}

	public Boolean getBoolean() {
		return false;
	}

	public Integer getInteger() {
		return 0;
	}

	public Float getFloat() {
		return 0f;
	}

	public Long getLong() {
		return 0L;
	}

	public String getString() {
		return "";
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
		return ta instanceof NullElValue ? El.TRUE : El.FALSE;
	}

	public ElValue isNEQ(ElValue ta) {
		return !(ta instanceof NullElValue) ? El.TRUE : El.FALSE;
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
		return "null";
	}

}
