package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;

public class Character2Number extends Castor<Character, Number> {

	@Override
	public Number cast(Character src, Type toType, String... args) {
		return (int) src.charValue();
	}

}
