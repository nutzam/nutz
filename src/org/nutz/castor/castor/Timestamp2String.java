package org.nutz.castor.castor;

import java.sql.Timestamp;

public class Timestamp2String extends DateTimeCastor<Timestamp, String> {

	@Override
	protected String cast(Timestamp src, Class<?> toType, String... args) {
		return dateTimeFormat.format(new java.util.Date(src.getTime()));
	}

}
