package com.zzh.castor.castor;

import java.util.TimeZone;

import com.zzh.castor.Castor;

public class String2TimeZone extends Castor<String, TimeZone> {

	@Override
	protected TimeZone cast(String src, Class<?> toType) {
		return TimeZone.getTimeZone(src);
	}

}
