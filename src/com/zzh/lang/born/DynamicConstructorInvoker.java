package com.zzh.lang.born;

import java.lang.reflect.Constructor;

import com.zzh.lang.Mirror;

public class DynamicConstructorInvoker<T> implements BorningInvoker<T> {

	private Constructor<T> c;
	private Object arg;

	public DynamicConstructorInvoker(Constructor<T> c, Object arg) {
		this.c = c;
		this.arg = arg;
	}

	public T born() throws Exception {
		return c.newInstance(arg);
	}

	@Override
	public T born(Object[] args) throws Exception {
		return c.newInstance(Mirror.evalArgToRealArray(args));
	}

	@Override
	public void clearArgs() {
		arg = null;
	}

}
