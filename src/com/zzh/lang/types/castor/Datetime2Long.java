package com.zzh.lang.types.castor;

import java.util.Date;

import com.zzh.lang.types.Castor;

public class Datetime2Long extends Castor<java.util.Date, Long> {

	@Override
	protected Long cast(Date src) {
		return src.getTime();
	}

}
