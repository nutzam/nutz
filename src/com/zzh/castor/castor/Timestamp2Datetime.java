package com.zzh.castor.castor;

import java.sql.Timestamp;
import java.util.Date;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Timestamp2Datetime extends Castor<Timestamp, java.util.Date> {

	@Override
	protected Date cast(Timestamp src, Class<?> toType) throws FailToCastObjectException {
		return new java.util.Date(src.getTime());
	}

}
