package org.nutz.castor.castor;

import java.io.InputStreamReader;
import java.lang.reflect.Array;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StringInputStream;

public class String2Array extends Castor<String, Object> {

	public String2Array() {
		this.fromClass = String.class;
		this.toClass = Array.class;
	}

	@Override
	protected Object cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		src = Strings.trim(src);
		StringBuilder sb = new StringBuilder();
		if (!src.startsWith("["))
			sb.append('[');
		sb.append(src);
		if (!src.endsWith("]"))
			sb.append(']');
		return Json.fromJson(toType, new InputStreamReader(new StringInputStream(sb)));
	}

}
