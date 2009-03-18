package com.zzh.castor.castor;

import java.util.Collection;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;
import com.zzh.json.JsonFormat;

@SuppressWarnings("unchecked")
public class Collection2String extends Castor<Collection, String> {

	@Override
	protected String cast(Collection src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Json.toJson(src, JsonFormat.compact());
	}

}
