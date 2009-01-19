package com.zzh.lang.types.castor;

import java.util.Calendar;

public class Calendar2String extends DateTimeCastor<Calendar,String> {

	@Override
	protected String cast(Calendar src) {
		return dateTimeFormat.format(((Calendar) src).getTime());
	}

}
