package com.zzh.castor.castor;

import java.sql.Timestamp;
import java.util.Calendar;

import com.zzh.castor.Castor;

public class Timestamp2Calendar extends Castor<Timestamp, Calendar> {

	@Override
	protected Calendar cast(Timestamp src, Class<?> toType) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(src.getTime());
		return c;
	}

}
