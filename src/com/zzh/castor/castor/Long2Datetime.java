package com.zzh.castor.castor;

import java.util.Date;

import com.zzh.castor.Castor;

public class Long2Datetime extends Castor<Long, java.util.Date> {

	@Override
	protected Date cast(Long src, Class<?> toType) {
		return new java.util.Date(src);
	}

}
