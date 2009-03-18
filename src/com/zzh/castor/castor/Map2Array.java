package com.zzh.castor.castor;

import java.lang.reflect.Array;
import java.util.Map;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public class Map2Array extends Castor<Map, Object> {

	public Map2Array() {
		this.fromClass = Map.class;
		this.toClass = Array.class;
	}

	@Override
	protected Object cast(Map src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Lang.collection2array(src.values(), toType.getComponentType());
	}

}
