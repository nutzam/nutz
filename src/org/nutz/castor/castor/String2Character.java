package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class String2Character extends Castor<String, Character> {

	@Override
	public Character cast(String src, Type toType, String... args)
			throws FailToCastObjectException {
		return src.charAt(0);
	}

}
