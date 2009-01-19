package com.zzh.lang.types.castor;

import java.text.ParseException;

import com.zzh.lang.Lang;

public class String2SqlTime extends DateTimeCastor<String, java.sql.Time> {

	@Override
	protected java.sql.Time cast(String src) {
		try {
			return new java.sql.Time(timeFormat.parse(src).getTime());
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
