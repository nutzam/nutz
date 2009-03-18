package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class String2Enum extends Castor<String, Enum> {

	@Override
	protected Enum cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Enum.valueOf((Class<Enum>) toType, src);
	}

}
