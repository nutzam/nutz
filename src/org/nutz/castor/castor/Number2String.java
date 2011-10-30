package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;

public class Number2String extends Castor<Number, String> {

	@Override
	public String cast(Number src, Type toType, String... args) {
		return src.toString();
	}

}
