package com.zzh.castor.castor;

import java.sql.Time;
import java.sql.Timestamp;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Timestamp2SqlTime extends Castor<Timestamp, Time> {

	@Override
	protected Time cast(Timestamp src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return new Time(src.getTime());
	}

}
