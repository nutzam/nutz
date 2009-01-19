package com.zzh.lang.types.castor;

import java.text.ParseException;
import java.util.Calendar;

import com.zzh.lang.Lang;

public class String2Calendar extends DateTimeCastor<String,Calendar> {

	@Override
	protected Calendar cast(String src) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(dateTimeFormat.parse((String) src));
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
		return c;
	}

}
