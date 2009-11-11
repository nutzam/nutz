package org.nutz.lang.born;

import java.lang.reflect.Constructor;

public class ConstructorBorning<T> implements Borning<T> {

	private Constructor<T> c;

	public ConstructorBorning(Constructor<T> c) {
		this.c = c;
	}

	public T born(Object[] args) {
		try {
			if (args == null)
				return c.newInstance();
			return c.newInstance(args);
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
