package com.zzh.castor.castor;

import java.util.Calendar;
import java.util.Date;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Calendar2Datetime extends Castor<java.util.Calendar, java.util.Date> {

	@Override
	protected Date cast(Calendar src, Class<?> toType) throws FailToCastObjectException {
		return src.getTime();
	}

}
