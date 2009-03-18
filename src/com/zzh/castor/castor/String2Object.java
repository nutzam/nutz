package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Mirror;

public class String2Object extends Castor<String, Object> {

	@Override
	protected Object cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Mirror.me(toType).born(src);
	}

}
