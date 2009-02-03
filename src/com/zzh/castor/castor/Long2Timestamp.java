package com.zzh.castor.castor;

import java.sql.Timestamp;

import com.zzh.castor.Castor;

public class Long2Timestamp extends Castor<Long, Timestamp> {

	@Override
	protected Timestamp cast(Long src, Class<?> toType) {
		return new Timestamp(src);
	}

}
