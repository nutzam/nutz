package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Mirror;

public class Object2Object extends Castor<Object, Object> {

	@Override
	protected Object cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Mirror.me(toType).born(src);
	}

}
