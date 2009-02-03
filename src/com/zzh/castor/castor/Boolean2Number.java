package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;

public class Boolean2Number extends Castor<Boolean, Number> {

	@Override
	protected Number cast(Boolean src, Class<?> toType) {
		try {
			return (Number) Mirror.me(toType).getWrpperClass().getConstructor(String.class)
					.newInstance(src ? "1" : "0");
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
