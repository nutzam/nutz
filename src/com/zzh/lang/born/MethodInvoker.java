package com.zzh.lang.born;

import java.lang.reflect.Method;

public class MethodInvoker<T> implements BorningInvoker<T> {

	private Method method;
	private Object[] args;

	public MethodInvoker(Method method, Object[] args) {
		this.method = method;
		this.args = args;
	}

	@SuppressWarnings("unchecked")
	public T born() throws Exception {
		return (T) method.invoke(null, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T born(Object[] args) throws Exception {
		return (T) method.invoke(null, args);
	}

	@Override
	public void clearArgs() {
		args = null;
	}

}
