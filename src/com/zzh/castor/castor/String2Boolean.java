package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class String2Boolean extends Castor<String, Boolean> {

	@Override
	protected Boolean cast(String src, Class<?> toType) throws FailToCastObjectException {
		return Boolean.valueOf(src);
	}

}
