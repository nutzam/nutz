package com.zzh.castor.castor;

import java.util.Collection;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;
import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public class String2Collection extends Castor<String, Collection> {

	@Override
	protected Collection cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return (Collection) Json.fromJson(toType, Lang.inr(src));
	}

}
