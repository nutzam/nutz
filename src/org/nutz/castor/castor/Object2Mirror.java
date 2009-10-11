package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Mirror;

@SuppressWarnings("unchecked")
public class Object2Mirror extends Castor<Object, Mirror> {

	@Override
	protected Mirror cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
		return Mirror.me(src.getClass());
	}

}
