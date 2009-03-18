package com.zzh.castor.castor;

import java.util.Date;

import com.zzh.castor.Castor;

public class Number2Datetime extends Castor<Number, java.util.Date> {

	@Override
	protected Date cast(Number src, Class<?> toType, String... args) {
		return new java.util.Date(src.longValue());
	}

}
