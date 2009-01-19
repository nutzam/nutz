package com.zzh.lang.types.castor;

import java.sql.Timestamp;
import java.util.Calendar;

import com.zzh.lang.types.Castor;

public class Timestamp2Calendar extends Castor<Timestamp, Calendar> {

	@Override
	protected Calendar cast(Timestamp src) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(src.getTime());
		return c;
	}

}
