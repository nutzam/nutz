package com.zzh.lang.born;

import java.lang.reflect.Method;

public class EmptyArgsMethodInvoker<T> implements BorningInvoker<T> {

	private Method method;

	public EmptyArgsMethodInvoker(Method method) {
		this.method = method;
	}

	@SuppressWarnings("unchecked")
	public T born() throws Exception {
		return (T) method.invoke(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T born(Object[] args) throws Exception {
		return (T) method.invoke(null);
	}

	@Override
	public void clearArgs() {}

}
