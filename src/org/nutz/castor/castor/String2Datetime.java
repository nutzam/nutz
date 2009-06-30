package org.nutz.castor.castor;

import java.text.ParseException;

import org.nutz.lang.Lang;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

	@Override
	protected java.util.Date cast(String src, Class<?> toType, String... args) {
		try {
			return dateTimeFormat.parse(src);
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
