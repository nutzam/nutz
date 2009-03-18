package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public class Object2Mirror extends Castor<Object, Mirror> {

	@Override
	protected Mirror cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Mirror.me(src.getClass());
	}

}
