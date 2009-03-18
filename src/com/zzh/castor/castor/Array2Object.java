package com.zzh.castor.castor;

import java.lang.reflect.Array;

import com.zzh.castor.Castor;
import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;

public class Array2Object extends Castor<Object, Object> {

	public Array2Object() {
		this.fromClass = Array.class;
		this.toClass = Object.class;
	}

	@Override
	protected Object cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		if (Array.getLength(src) == 0)
			return null;
		return Castors.me().castTo(Array.get(src, 0), toType);
	}

}
