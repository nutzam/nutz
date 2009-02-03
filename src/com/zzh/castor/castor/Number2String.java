package com.zzh.castor.castor;

import com.zzh.castor.Castor;

public class Number2String extends Castor<Number, String> {

	@Override
	protected String cast(Number src, Class<?> toType) {
		return src.toString();
	}

}
