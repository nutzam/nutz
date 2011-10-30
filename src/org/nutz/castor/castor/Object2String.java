package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Object2String extends Castor<Object, String> {

	@Override
	public String cast(Object src, Type toType, String... args)
			throws FailToCastObjectException {
		return src.toString();
	}

}
