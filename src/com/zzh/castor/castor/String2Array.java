package com.zzh.castor.castor;

import java.lang.reflect.Array;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;
import com.zzh.lang.Strings;
import com.zzh.lang.stream.CharInputStream;

public class String2Array extends Castor<String, Object> {

	public String2Array() {
		this.fromClass = String.class;
		this.toClass = Array.class;
	}

	@Override
	protected Object cast(String src, Class<?> toType) throws FailToCastObjectException {
		src = Strings.trim(src);
		StringBuilder sb = new StringBuilder();
		if (!src.startsWith("["))
			sb.append('[');
		sb.append(src);
		if (!src.endsWith("]"))
			sb.append(']');
		return Json.fromJson(toType, new CharInputStream(sb));
	}

}
