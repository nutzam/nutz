package com.zzh.castor.castor;

import java.lang.reflect.Array;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;
import com.zzh.json.JsonFormat;

public class Array2String extends Castor<Object, String> {

	public Array2String() {
		this.fromClass = Array.class;
		this.toClass = String.class;
	}

	@Override
	protected String cast(Object src, Class<?> toType) throws FailToCastObjectException {
		return Json.toJson(src, JsonFormat.compact());
	}

}
