package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public class Mirror2String extends Castor<Mirror, String> {

	@Override
	protected String cast(Mirror src, Class<?> toType) {
		return src.getMyClass().getName();
	}

}
