package org.nutz.lang.born;

import java.lang.reflect.Method;

public class EmptyArgsMethodInvoker<T> implements BorningInvoker<T> {

	private Method method;

	public EmptyArgsMethodInvoker(Method method) {
		this.method = method;
	}

	@SuppressWarnings("unchecked")
	public T born() {
		try {
			return (T) method.invoke(null);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public T born(Object[] args) {
		try {
			return (T) method.invoke(null);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
