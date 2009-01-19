package com.zzh.lang.types.castor;

import com.zzh.lang.Lang;
import com.zzh.lang.types.Castor;

public class String2Class extends Castor<String, Class<?>> {

	@Override
	protected Class<?> cast(String src) {
		try {
			return Class.forName(src);
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
