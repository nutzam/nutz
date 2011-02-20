package org.nutz.castor.castor;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

import org.nutz.lang.Lang;

public class String2Timestamp extends DateTimeCastor<String, Timestamp> {

	@Override
	public Timestamp cast(String src, Class<?> toType, String... args) {
		try {
			return new java.sql.Timestamp(((DateFormat) dateTimeFormat.clone()).parse(src)
																				.getTime());
		}
		catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
