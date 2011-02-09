package org.nutz.el.val;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;
import org.nutz.lang.Mirror;

public class PojoElValue<T> implements ElValue {

	protected T obj;

	protected Mirror<T> mirror;

	public PojoElValue(T obj) {
		this.obj = obj;
		this.mirror = Mirror.me(obj);
	}

	public Object get() {
		return obj;
	}

	public Boolean getBoolean() {
		throw new ElException("%s has not Boolean value", getClass().getSimpleName());
	}

	public Integer getInteger() {
		throw new ElException("%s has not Integer value", getClass().getSimpleName());
	}

	public Float getFloat() {
		throw new ElException("%s has not Float value", getClass().getSimpleName());
	}

	public Long getLong() {
		throw new ElException("%s has not Long value", getClass().getSimpleName());
	}

	public ElValue plus(ElValue ta) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "plus");
	}

	public String getString() {
		throw new ElException("%s has not String value", getClass().getSimpleName());
	}

	public ElValue invoke(ElValue[] args) {
		String name = args[0].getString();
		Object[] params = new Object[args.length - 1];
		for (int i = 0; i < params.length; i++) {
			params[i] = args[i + 1].get();
		}
		return El.wrap(Mirror.me(obj).invoke(obj, name, params));
	}

	public ElValue getProperty(ElValue val) {
		return El.wrap(mirror.getValue(obj, val.getString()));
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
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "isEquals");
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
		return null == obj ? "null" : obj.toString();
	}

}
