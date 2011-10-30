package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;

public class Timestamp2String extends DateTimeCastor<Timestamp, String> {

	@Override
	public String cast(Timestamp src, Type toType, String... args) {
		return ((DateFormat) dateTimeFormat.clone()).format(new java.util.Date(src.getTime()));
	}

}
