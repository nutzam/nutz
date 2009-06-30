package org.nutz.castor.castor;

public class SqlTime2String extends DateTimeCastor<java.sql.Time, String> {

	@Override
	protected String cast(java.sql.Time src, Class<?> toType, String... args) {
		return timeFormat.format(new java.util.Date(src.getTime()));
	}

}
