package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Boolean2String extends Castor<Boolean, String> {

	@Override
	protected String cast(Boolean src, Class<?> toType) throws FailToCastObjectException {
		return String.valueOf(src);
	}

}
