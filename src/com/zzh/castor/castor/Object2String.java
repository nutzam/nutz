package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Object2String extends Castor<Object, String> {

	@Override
	protected String cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return src.toString();
	}

}
