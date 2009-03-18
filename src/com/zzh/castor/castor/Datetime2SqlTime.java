package com.zzh.castor.castor;

import java.sql.Time;
import java.util.Date;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Datetime2SqlTime extends Castor<Date, Time> {

	@Override
	protected Time cast(Date src, Class<?> toType, String... args) throws FailToCastObjectException {
		return new Time(src.getTime());
	}

}
