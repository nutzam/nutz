package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class Object2Class extends Castor<Object, Class> {

	@Override
	protected Class cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return src.getClass();
	}

}
