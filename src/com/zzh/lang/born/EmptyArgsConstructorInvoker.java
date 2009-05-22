package com.zzh.lang.born;

import java.lang.reflect.Constructor;

public class EmptyArgsConstructorInvoker<T> implements BorningInvoker<T> {
	
	private Constructor<T> c;

	public EmptyArgsConstructorInvoker(Constructor<T> c) {
		this.c = c;
	}

	public T born() throws Exception {
		return c.newInstance();
	}

	@Override
	public T born(Object[] args) throws Exception {
		return c.newInstance();
	}

	@Override
	public void clearArgs() {}
	
}
