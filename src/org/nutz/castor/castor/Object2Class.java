package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class Object2Class extends Castor<Object, Class> {

	@Override
	public Class cast(Object src, Type toType, String... args) throws FailToCastObjectException {
		return src.getClass();
	}

}
