package org.nutz.lang.born;

import java.lang.reflect.Constructor;

import org.nutz.lang.Mirror;

public class DynamicConstructorBorning<T> implements Borning<T> {

	private Constructor<T> c;

	public DynamicConstructorBorning(Constructor<T> c) {
		this.c = c;
	}

	public T born(Object[] args) {
		try {
			return c.newInstance(Mirror.evalArgToRealArray(args));
		} catch (Exception e) {
			throw new BorningException(e);
		}
	}

}
