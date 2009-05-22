package com.zzh.lang.born;

import java.lang.reflect.Constructor;

public class ConstructorInvoker<T> implements BorningInvoker<T> {

	private Constructor<T> c;
	private Object[] args;

	public ConstructorInvoker(Constructor<T> c, Object[] args) {
		this.c = c;
		this.args = args;
	}

	public T born() throws Exception {
		return c.newInstance(args);
	}

	@Override
	public T born(Object[] args) throws Exception {
		return c.newInstance(args);
	}

	@Override
	public void clearArgs() {
		args = null;
	}

}
