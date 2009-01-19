package com.zzh.lang.types.castor;

import java.util.TimeZone;

import com.zzh.lang.types.Castor;

public class TimeZone2String extends Castor<TimeZone, String> {

	@Override
	protected String cast(TimeZone src) {
		return ((TimeZone) src).getID();
	}

}
