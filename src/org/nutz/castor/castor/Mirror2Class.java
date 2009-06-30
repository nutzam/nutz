package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Mirror;

@SuppressWarnings("unchecked")
public class Mirror2Class extends Castor<Mirror, Class> {

	@Override
	protected Class cast(Mirror src, Class toType, String... args) {
		return src.getType();
	}

}
