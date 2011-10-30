package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Mirror;

@SuppressWarnings({"rawtypes"})
public class Object2Mirror extends Castor<Object, Mirror> {

	@Override
	public Mirror cast(Object src, Type toType, String... args)
			throws FailToCastObjectException {
		return Mirror.me(src.getClass());
	}

}
