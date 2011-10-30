package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class String2Pattern extends Castor<String, Pattern> {

	@Override
	public Pattern cast(String src, Type toType, String... args)
			throws FailToCastObjectException {
		try {
			return Pattern.compile(src);
		}
		catch (Exception e) {
			throw new FailToCastObjectException("Error regex: " + src, e);
		}
	}

}
