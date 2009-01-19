package com.zzh.lang.types.castor;

import java.text.ParseException;

import com.zzh.lang.Lang;

public class String2SqlDate extends DateTimeCastor<String,java.sql.Date> {

	@Override
	protected java.sql.Date cast(String src) {
		try {
			return new java.sql.Date(dateFormat.parse((String) src).getTime());
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
