package org.nutz.lang.born;

import java.lang.reflect.Constructor;

public class EmptyArgsConstructorBorning<T> implements Borning<T> {

	private Constructor<T> c;

	public EmptyArgsConstructorBorning(Constructor<T> c) {
		this.c = c;
	}

	public T born(Object[] args) {
		try {
			return c.newInstance();
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
