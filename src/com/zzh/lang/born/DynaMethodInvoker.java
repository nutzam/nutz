package com.zzh.lang.born;

import java.lang.reflect.Method;

import com.zzh.lang.Mirror;

public class DynaMethodInvoker<T> implements BorningInvoker<T> {

	private Method method;
	private Object arg;

	public DynaMethodInvoker(Method method, Object arg) {
		this.method = method;
		this.arg = arg;
	}

	@SuppressWarnings("unchecked")
	public T born() throws Exception {
		return (T) method.invoke(null, arg);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T born(Object[] args) throws Exception {
		return (T) method.invoke(null, Mirror.evalArgToRealArray(args));
	}

	@Override
	public void clearArgs() {
		arg = null;
	}

}
