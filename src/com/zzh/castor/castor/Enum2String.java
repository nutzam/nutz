package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class Enum2String extends Castor<Enum, String> {

	@Override
	protected String cast(Enum src, Class<?> toType) throws FailToCastObjectException {
		return src.name();
	}
}
