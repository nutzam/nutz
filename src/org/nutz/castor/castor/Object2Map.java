package org.nutz.castor.castor;

import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

@SuppressWarnings("unchecked")
public class Object2Map extends Castor<Object, Map> {

	@Override
	public Map cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
		StringBuilder sb = new StringBuilder(Json.toJson(src));
		Map map = (Map) Json.fromJson(Lang.inr(sb));
		return map;
	}

}
