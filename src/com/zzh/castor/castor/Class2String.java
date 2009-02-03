package com.zzh.castor.castor;

import com.zzh.castor.Castor;

@SuppressWarnings("unchecked")
public class Class2String extends Castor<Class, String> {

	@Override
	protected String cast(Class src, Class<?> toType) {
		return src.getName();
	}

}
