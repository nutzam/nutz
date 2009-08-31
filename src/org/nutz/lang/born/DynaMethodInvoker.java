package org.nutz.lang.born;

import java.lang.reflect.Method;

import org.nutz.lang.Mirror;

public class DynaMethodInvoker<T> implements BorningInvoker<T> {

	private Method method;
	private Object arg;

	public DynaMethodInvoker(Method method, Object arg) {
		this.method = method;
		this.arg = arg;
	}

	@SuppressWarnings("unchecked")
	public T born() {
		try {
			return (T) method.invoke(null, arg);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public T born(Object[] args) {
		try {
			return (T) method.invoke(null, Mirror.evalArgToRealArray(args));
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
