package org.nutz.lang.born;

import java.lang.reflect.Method;

public class EmptyArgsMethodBorning<T> implements Borning<T> {

	private Method method;

	public EmptyArgsMethodBorning(Method method) {
		this.method = method;
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
