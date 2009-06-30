package org.nutz.castor.castor;

import org.nutz.castor.Castor;

public class Character2Number extends Castor<Character, Number> {

	@Override
	protected Number cast(Character src, Class<?> toType, String... args) {
		return (int) src.charValue();
	}

}
