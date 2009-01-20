package com.zzh.lang.types.castor;

import java.util.Date;

import com.zzh.lang.types.Castor;

public class Long2Datetime extends Castor<Long, java.util.Date> {

	@Override
	protected Date cast(Long src) {
		return new java.util.Date(src);
	}

}
