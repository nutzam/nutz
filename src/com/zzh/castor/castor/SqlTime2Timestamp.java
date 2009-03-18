package com.zzh.castor.castor;

import java.sql.Time;
import java.sql.Timestamp;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class SqlTime2Timestamp extends Castor<Time, Timestamp> {

	@Override
	protected Timestamp cast(Time src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return new Timestamp(src.getTime());
	}

}
