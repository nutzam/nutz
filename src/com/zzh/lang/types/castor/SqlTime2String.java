package com.zzh.lang.types.castor;

public class SqlTime2String extends DateTimeCastor<java.sql.Time, String> {

	@Override
	protected String cast(java.sql.Time src) {
		return timeFormat.format(new java.util.Date(src.getTime()));
	}

}
