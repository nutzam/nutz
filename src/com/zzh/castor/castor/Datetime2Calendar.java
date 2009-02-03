package com.zzh.castor.castor;

import java.util.Calendar;
import java.util.Date;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Datetime2Calendar extends Castor<java.util.Date, java.util.Calendar> {

	@Override
	protected Calendar cast(Date src, Class<?> toType) throws FailToCastObjectException {
		Calendar c = Calendar.getInstance();
		c.setTime(src);
		return c;
	}

}
