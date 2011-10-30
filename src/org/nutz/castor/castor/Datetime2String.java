package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.text.DateFormat;

public class Datetime2String extends DateTimeCastor<java.util.Date, String> {

	@Override
	public String cast(java.util.Date src, Type toType, String... args) {
		return ((DateFormat) dateTimeFormat.clone()).format(src);
	}

}
