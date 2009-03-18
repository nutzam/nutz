package com.zzh.castor.castor;

import java.util.Date;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Datetime2SqlDate extends Castor<Date, java.sql.Date> {

	@Override
	protected java.sql.Date cast(Date src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return new java.sql.Date(src.getTime());
	}

}
