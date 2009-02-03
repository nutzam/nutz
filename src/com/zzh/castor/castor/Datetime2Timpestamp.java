package com.zzh.castor.castor;

import java.sql.Timestamp;
import java.util.Date;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Datetime2Timpestamp extends Castor<java.util.Date, Timestamp> {

	@Override
	protected Timestamp cast(Date src, Class<?> toType) throws FailToCastObjectException {
		return new Timestamp(src.getTime());
	}

}
