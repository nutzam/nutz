package org.nutz.castor.castor;

import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;

@SuppressWarnings("unchecked")
public class Object2Map extends Castor<Object, Map> {

	@Override
	public Map cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
		return (Map) Lang.obj2map(src, (Class<? extends Map>) ((Class<? extends Map>) toType));
	}

}
