package org.nutz.lang.born;

import java.lang.reflect.Constructor;

public class ConstructorInvoker<T> implements BorningInvoker<T> {

	private Constructor<T> c;
	private Object[] args;

	public ConstructorInvoker(Constructor<T> c, Object[] args) {
		this.c = c;
		this.args = args;
	}

	public T born() {
		return born(args);
	}

	public T born(Object[] args) {
		try {
			if (args == null)
				return c.newInstance();
			return c.newInstance(args);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
