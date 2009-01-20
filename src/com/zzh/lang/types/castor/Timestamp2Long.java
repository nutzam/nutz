package com.zzh.lang.types.castor;

import java.sql.Timestamp;

import com.zzh.lang.types.Castor;

public class Timestamp2Long extends Castor<Timestamp, Long> {

	@Override
	protected Long cast(Timestamp src) {
		return src.getTime();
	}

}
