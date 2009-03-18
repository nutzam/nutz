package com.zzh.castor.castor;

import java.sql.Date;
import java.sql.Timestamp;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class SqlDate2Timestamp extends Castor<Date, Timestamp> {

	@Override
	protected Timestamp cast(Date src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return new Timestamp(src.getTime());
	}

}
