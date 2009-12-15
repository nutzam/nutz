package org.nutz.castor.castor;

import java.text.ParseException;

import org.nutz.lang.Lang;

public class String2SqlTime extends DateTimeCastor<String, java.sql.Time> {

	@Override
	public java.sql.Time cast(String src, Class<?> toType, String... args) {
		try {
			return new java.sql.Time(timeFormat.parse(src).getTime());
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
