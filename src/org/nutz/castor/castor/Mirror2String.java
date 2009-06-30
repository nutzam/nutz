package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Mirror;

@SuppressWarnings("unchecked")
public class Mirror2String extends Castor<Mirror, String> {

	@Override
	protected String cast(Mirror src, Class<?> toType, String... args) {
		return src.getType().getName();
	}

}
