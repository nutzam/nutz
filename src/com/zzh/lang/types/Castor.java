package com.zzh.lang.types;

import com.zzh.lang.Mirror;

public abstract class Castor<FROM, TO> {

	protected Castor() {
		fromClass = (Class<?>) Mirror.getTypeParams(getClass())[0];
		toClass = (Class<?>) Mirror.getTypeParams(getClass())[1];
	}

	private Class<?> fromClass;
	private Class<?> toClass;

	public Class<?> getFromClass() {
		return fromClass;
	}

	public Class<?> getToClass() {
		return toClass;
	}

	protected abstract Object cast(Object src);
}
