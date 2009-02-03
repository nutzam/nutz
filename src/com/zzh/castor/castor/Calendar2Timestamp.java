package com.zzh.castor.castor;

import java.util.Calendar;
import java.sql.Timestamp;

import com.zzh.castor.Castor;

public class Calendar2Timestamp extends Castor<Calendar, Timestamp> {

	@Override
	protected Timestamp cast(Calendar src, Class<?> toType) {
		long ms = src.getTimeInMillis();
		return new Timestamp(ms);
	}
}
