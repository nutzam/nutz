package com.zzh.lang.types.castor;

import java.util.TimeZone;

import com.zzh.lang.types.Castor;


public class String2TimeZone extends Castor<String,TimeZone> {

	@Override
	protected Object cast(Object src) {
		return TimeZone.getTimeZone((String) src);
	}

}
