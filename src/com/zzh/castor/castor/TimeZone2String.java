package com.zzh.castor.castor;

import java.util.TimeZone;

import com.zzh.castor.Castor;

public class TimeZone2String extends Castor<TimeZone, String> {

	@Override
	protected String cast(TimeZone src, Class<?> toType) {
		return src.getID();
	}

}
