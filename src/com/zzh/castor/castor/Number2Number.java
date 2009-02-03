package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;

public class Number2Number extends Castor<Number, Number> {

	@Override
	protected Number cast(Number src, Class<?> toType) {
		try {
			return (Number) Mirror.me(toType).getWrpperClass().getConstructor(String.class)
					.newInstance(src.toString());
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
