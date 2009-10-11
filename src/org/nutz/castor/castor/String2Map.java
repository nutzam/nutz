package org.nutz.castor.castor;

import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

@SuppressWarnings("unchecked")
public class String2Map extends Castor<String, Map> {

	@Override
	protected Map cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
		return (Map) Json.fromJson(Lang.inr(src));
	}

}
