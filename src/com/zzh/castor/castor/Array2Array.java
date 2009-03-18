package com.zzh.castor.castor;

import java.lang.reflect.Array;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Lang;

public class Array2Array<T> extends Castor<Object, Object> {

	public Array2Array() {
		this.fromClass = Array.class;
		this.toClass = Array.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Lang.array2array(src, (Class<T[]>) toType);
	}

}
