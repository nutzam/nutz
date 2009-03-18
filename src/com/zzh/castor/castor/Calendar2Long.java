package com.zzh.castor.castor;

import java.util.Calendar;

import com.zzh.castor.Castor;

public class Calendar2Long extends Castor<Calendar, Long> {
	@Override
	protected Long cast(Calendar src, Class<?> toType, String... args) {
		return src.getTimeInMillis();
	}
}
