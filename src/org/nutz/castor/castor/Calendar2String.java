package org.nutz.castor.castor;

import java.util.Calendar;

public class Calendar2String extends DateTimeCastor<Calendar, String> {

	@Override
	protected String cast(Calendar src, Class<?> toType, String... args) {
		return dateTimeFormat.format(src.getTime());
	}

}
