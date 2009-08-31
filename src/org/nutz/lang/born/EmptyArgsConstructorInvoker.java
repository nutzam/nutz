package org.nutz.lang.born;

import java.lang.reflect.Constructor;

public class EmptyArgsConstructorInvoker<T> implements BorningInvoker<T> {

	private Constructor<T> c;

	public EmptyArgsConstructorInvoker(Constructor<T> c) {
		this.c = c;
	}

	public T born() {
		try {
			return c.newInstance();
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

	public T born(Object[] args) {
		try {
			return c.newInstance();
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
