package com.zzh.castor;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import com.zzh.lang.Mirror;

public abstract class Castor<FROM, TO> {

	protected Castor() {
		fromClass = (Class<?>) Mirror.getTypeParams(getClass())[0];
		toClass = (Class<?>) Mirror.getTypeParams(getClass())[1];
	}

	Castors castors;

	protected Castors getCastors() {
		return castors;
	}

	protected Class<?> fromClass;
	protected Class<?> toClass;

	public Class<?> getFromClass() {

		return fromClass;
	}

	public Class<?> getToClass() {
		return toClass;
	}

	protected abstract TO cast(FROM src, Class<?> toType) throws FailToCastObjectException;

	@SuppressWarnings("unchecked")
	protected static Collection createCollection(Object src, Class<?> toType)
			throws FailToCastObjectException {
		Collection coll = null;
		try {
			coll = (Collection<Object>) toType.newInstance();
		} catch (Exception e) {
			if (Modifier.isAbstract(toType.getModifiers())
					&& toType.isAssignableFrom(ArrayList.class)) {
				coll = new ArrayList<Object>(Array.getLength(src));
			}
			if (null == coll)
				throw new FailToCastObjectException(String.format(
						"Castors don't know how to implement '%s'", toType.getName()));
		}
		return coll;
	}
}
