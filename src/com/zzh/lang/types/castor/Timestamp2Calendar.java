package com.zzh.lang.types.castor;

import java.sql.Timestamp;
import java.util.Calendar;

import com.zzh.lang.types.Castor;


public class Timestamp2Calendar extends Castor<Timestamp,Calendar> {

	@Override
	protected Object cast(Object src) {
		Calendar c = Calendar.getInstance();
		long ms = ((Timestamp) src).getTime();
		c.setTimeInMillis(ms);
		return c;
	}

}
