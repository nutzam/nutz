package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public class String2Class extends Castor<String, Class> {

	public String2Class() {
		fromClass = String.class;
		toClass = Class.class;
	}

	@Override
	protected Class<?> cast(String src, Class toType) {
		try {
			return Class.forName(src);
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
