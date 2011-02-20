package org.nutz.castor.castor;

import java.text.DateFormat;
import java.text.ParseException;

import org.nutz.lang.Lang;

public class String2SqlTime extends DateTimeCastor<String, java.sql.Time> {

	@Override
	public java.sql.Time cast(String src, Class<?> toType, String... args) {
		try {
			return new java.sql.Time(((DateFormat) timeFormat.clone()).parse(src).getTime());
		}
		catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
