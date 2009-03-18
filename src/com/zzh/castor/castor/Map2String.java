package com.zzh.castor.castor;

import java.util.Map;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;

@SuppressWarnings("unchecked")
public class Map2String extends Castor<Map, String> {

	@Override
	protected String cast(Map src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Json.toJson(src);
	}

}
