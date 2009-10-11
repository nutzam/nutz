package org.nutz.castor.castor;

import java.lang.reflect.Array;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class Array2String extends Castor<Object, String> {

	public Array2String() {
		this.fromClass = Array.class;
		this.toClass = String.class;
	}

	@Override
	protected String cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
		return Json.toJson(src, JsonFormat.compact());
	}

}
