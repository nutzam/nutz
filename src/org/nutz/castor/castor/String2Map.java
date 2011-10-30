package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

@SuppressWarnings({"rawtypes"})
public class String2Map extends Castor<String, Map> {

	@Override
	public Map cast(String src, Type toType, String... args) throws FailToCastObjectException {
		return (Map) Json.fromJson(Lang.inr(src));
	}

}
