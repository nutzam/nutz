package com.zzh.lang.types.castor;

import com.zzh.lang.types.Castor;

public class Class2String extends Castor<Class<?>, String> {

	@Override
	protected String cast(Class<?> src) {
		return src.getName();
	}

}
