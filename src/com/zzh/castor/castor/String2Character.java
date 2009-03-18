package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class String2Character extends Castor<String, Character> {

	@Override
	protected Character cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return src.charAt(0);
	}

}
