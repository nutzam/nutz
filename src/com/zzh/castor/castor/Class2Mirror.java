package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public class Class2Mirror extends Castor<Class, Mirror> {

	@Override
	protected Mirror<?> cast(Class src, Class toType) {
		return Mirror.me(src);
	}

}
