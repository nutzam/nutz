package com.zzh.castor.castor;

import java.util.Calendar;

import com.zzh.castor.Castor;

public class Long2Calendar extends Castor<Long, Calendar> {

	@Override
	protected Calendar cast(Long src, Class<?> toType) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(src);
		return c;
	}

}
