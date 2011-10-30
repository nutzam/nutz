package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Mirror;

public class Object2Object extends Castor<Object, Object> {

	@Override
	public Object cast(Object src, Type toType, String... args)
			throws FailToCastObjectException {
		return Mirror.me(toType).born(src);
	}

}
