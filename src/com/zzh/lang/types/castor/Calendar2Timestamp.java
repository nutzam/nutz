package com.zzh.lang.types.castor;

import java.util.Calendar;
import java.sql.Timestamp;

import com.zzh.lang.types.Castor;

public class Calendar2Timestamp extends Castor<Calendar, Timestamp> {

	@Override
	protected Timestamp cast(Calendar src) {
		long ms = src.getTimeInMillis();
		return new Timestamp(ms);
	}
}
