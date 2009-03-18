package com.zzh.castor.castor;

import java.sql.Timestamp;

import com.zzh.castor.Castor;

public class Number2Timestamp extends Castor<Number, Timestamp> {

	@Override
	protected Timestamp cast(Number src, Class<?> toType, String... args) {
		return new Timestamp(src.longValue());
	}

}
