package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public class String2Mirror extends Castor<String, Mirror> {

	@Override
	protected Mirror<?> cast(String src, Class<?> toType) {
		try {
			return Mirror.me(Class.forName(src));
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
