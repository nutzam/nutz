package com.zzh.lang.types.castor;

import java.util.Calendar;

import com.zzh.lang.types.Castor;

public class Calendar2Long extends Castor<Calendar, Long> {
	@Override
	protected Long cast(Calendar src) {
		return src.getTimeInMillis();
	}
}
