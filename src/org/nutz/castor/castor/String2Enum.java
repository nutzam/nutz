package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class String2Enum extends Castor<String, Enum> {

	@Override
	protected Enum cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
		return Enum.valueOf((Class<Enum>) toType, src);
	}

}
