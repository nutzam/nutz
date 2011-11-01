package org.nutz.castor.castor;

import java.text.DateFormat;
import java.text.ParseException;

import org.nutz.lang.Lang;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

	@Override
	public java.util.Date cast(String src, Class<?> toType, String... args) {
	    if(src == null || "".equals(src)){
	        return null;
	    }
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
