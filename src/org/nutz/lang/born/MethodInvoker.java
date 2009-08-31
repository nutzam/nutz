package org.nutz.lang.born;

import java.lang.reflect.Method;

public class MethodInvoker<T> implements BorningInvoker<T> {

	private Method method;
	private Object[] args;

	public MethodInvoker(Method method, Object[] args) {
		this.method = method;
		this.args = args;
	}

	@SuppressWarnings("unchecked")
	public T born() {
		try {
			return (T) method.invoke(null, args);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public T born(Object[] args) {
		try {
			return (T) method.invoke(null, args);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
