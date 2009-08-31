package org.nutz.lang.born;

import java.lang.reflect.Constructor;

import org.nutz.lang.Mirror;

public class DynamicConstructorInvoker<T> implements BorningInvoker<T> {

	private Constructor<T> c;
	private Object args;

	public DynamicConstructorInvoker(Constructor<T> c, Object arg) {
		this.c = c;
		this.args = arg;
	}

	public T born() {
		try {
			return c.newInstance(args);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

	public T born(Object[] args) {
		try {
			return c.newInstance(Mirror.evalArgToRealArray(args));
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
