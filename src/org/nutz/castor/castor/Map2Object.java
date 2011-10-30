package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;

@SuppressWarnings({"rawtypes"})
public class Map2Object extends Castor<Map, Object> {

	@Override
	public Object cast(Map src, Type toType, String... args) throws FailToCastObjectException {
		return Lang.map2Object(src, Lang.getTypeClass(toType));
	}

}
