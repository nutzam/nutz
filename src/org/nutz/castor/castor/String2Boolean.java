package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class String2Boolean extends Castor<String, Boolean> {

	@Override
	protected Boolean cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Boolean.valueOf(src);
	}

}
