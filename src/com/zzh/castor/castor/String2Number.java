package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

public class String2Number extends Castor<String, Number> {

	@Override
	protected Number cast(String src, Class<?> toType, String... args) {
		try {
			return (Number) Mirror.me(toType).getWrpperClass().getConstructor(String.class)
					.newInstance(Strings.isBlank(src) ? "0" : src);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
