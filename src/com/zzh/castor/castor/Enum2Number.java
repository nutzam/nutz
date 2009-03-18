package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public class Enum2Number extends Castor<Enum, Number> {

	@Override
	protected Number cast(Enum src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		Mirror<?> mirror = Mirror.me(Integer.class);
		Integer re = src.ordinal();
		if (mirror.canCastToDirectly(toType))
			return re;
		return (Number) Mirror.me(toType).born(re.toString());
	}

}
