package org.nutz.el.val;

import org.nutz.el.ElException;
import org.nutz.el.ElValue;

public class BooleanElValue implements ElValue {

	private Boolean val;

	public BooleanElValue(Boolean val) {
		this.val = val;
	}

	public ElValue invoke(ElValue[] args) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "invoke");
	}

	public ElValue getProperty(ElValue val) {
		throw new ElException(	"%s don't support [%s]!",
								getClass().getSimpleName(),
								"getProperty");
	}

	public Object get() {
		return val;
	}

	public Boolean getBoolean() {
		return val;
	}

	public Integer getInteger() {
		return 1;
	}

	public Float getFloat() {
		return 1f;
	}

	public Long getLong(){
		return 1L;
	}
	
	public String getString() {
		return val.toString();
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
		return new BooleanElValue(val.equals(ta.getBoolean()));
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
		return val.toString();
	}

}
