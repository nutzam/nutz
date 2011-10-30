package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;

@SuppressWarnings({"rawtypes"})
public class Map2String extends Castor<Map, String> {

	@Override
	public String cast(Map src, Type toType, String... args) throws FailToCastObjectException {
		return Json.toJson(src);
	}

}
