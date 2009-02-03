package com.zzh.castor.castor;

import java.util.Calendar;

public class Calendar2String extends DateTimeCastor<Calendar, String> {

	@Override
	protected String cast(Calendar src, Class<?> toType) {
		return dateTimeFormat.format(src.getTime());
	}

}
