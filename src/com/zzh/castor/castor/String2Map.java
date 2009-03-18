package com.zzh.castor.castor;

import java.util.Map;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;
import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public class String2Map extends Castor<String, Map> {

	@Override
	protected Map cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return (Map) Json.fromJson(Lang.inr(src));
	}

}
