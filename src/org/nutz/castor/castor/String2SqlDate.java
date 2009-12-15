package org.nutz.castor.castor;

import java.text.ParseException;

import org.nutz.lang.Lang;

public class String2SqlDate extends DateTimeCastor<String, java.sql.Date> {

	@Override
	public java.sql.Date cast(String src, Class<?> toType, String... args) {
		try {
			return new java.sql.Date(dateFormat.parse(src).getTime());
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
