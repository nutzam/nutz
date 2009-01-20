package com.zzh.lang.types.castor;

import java.util.Calendar;

import com.zzh.lang.types.Castor;

public class Long2Calendar extends Castor<Long, Calendar> {

	@Override
	protected Calendar cast(Long src) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(src);
		return c;
	}

}
