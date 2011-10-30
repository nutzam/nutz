package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

public class String2Object extends Castor<String, Object> {

	@Override
	public Object cast(String src, Type toType, String... args)
			throws FailToCastObjectException {
		if (Strings.isQuoteByIgnoreBlank(src, '{', '}'))
			return Json.fromJson(toType, src);
		return Mirror.me(toType).born(src);
	}

}
