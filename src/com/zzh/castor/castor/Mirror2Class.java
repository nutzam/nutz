package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public class Mirror2Class extends Castor<Mirror, Class> {

	@Override
	protected Class cast(Mirror src, Class toType, String... args) {
		return src.getType();
	}

}
