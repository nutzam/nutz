package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

public class String2Number extends Castor<String, Number> {

	@Override
	protected Number cast(String src, Class<?> toType, String... args) {
		try {
			return (Number) Mirror.me(toType).getWrapperClass().getConstructor(String.class).newInstance(
					Strings.isBlank(src) ? "0" : src);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
