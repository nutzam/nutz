package com.zzh.castor.castor;

public class SqlTime2String extends DateTimeCastor<java.sql.Time, String> {

	@Override
	protected String cast(java.sql.Time src, Class<?> toType) {
		return timeFormat.format(new java.util.Date(src.getTime()));
	}

}
