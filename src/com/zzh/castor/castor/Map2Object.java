package com.zzh.castor.castor;

import java.util.Map;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public class Map2Object extends Castor<Map, Object> {

	@Override
	protected Object cast(Map src, Class<?> toType) throws FailToCastObjectException {
		return Lang.map2Object(src, toType, this.getCastors());
	}

}
