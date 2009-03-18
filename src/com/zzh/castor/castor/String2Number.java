package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;

public class String2Number extends Castor<String, Number> {

	@Override
	protected Number cast(String src, Class<?> toType, String... args) {
		try {
			return (Number) Mirror.me(toType).getWrpperClass().getConstructor(String.class)
					.newInstance(src);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
