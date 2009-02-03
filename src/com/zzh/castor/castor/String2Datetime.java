package com.zzh.castor.castor;

import java.text.ParseException;

import com.zzh.lang.Lang;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

	@Override
	protected java.util.Date cast(String src, Class<?> toType) {
		try {
			return dateTimeFormat.parse(src);
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
