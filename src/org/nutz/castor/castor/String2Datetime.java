package org.nutz.castor.castor;

import java.text.DateFormat;
import java.text.ParseException;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

	@Override
	public java.util.Date cast(String src, Class<?> toType, String... args) {
		if (Strings.isBlank(src))
			return null;
		try {
			return ((DateFormat) dateTimeFormat.clone()).parse(src);
		}
		catch (ParseException e1) {
			try {
				return ((DateFormat) dateFormat.clone()).parse(src);
			}
			catch (ParseException e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

}
