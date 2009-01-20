package com.zzh.lang.types.castor;

import java.sql.Timestamp;

import com.zzh.lang.types.Castor;

public class Long2Timestamp extends Castor<Long, Timestamp> {

	@Override
	protected Timestamp cast(Long src) {
		return new Timestamp(src);
	}

}
