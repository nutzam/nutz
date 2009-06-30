package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Mirror;

public class String2Object extends Castor<String, Object> {

	@Override
	protected Object cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Mirror.me(toType).born(src);
	}

}
