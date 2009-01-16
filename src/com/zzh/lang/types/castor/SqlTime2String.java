package com.zzh.lang.types.castor;

public class SqlTime2String extends DateTimeCastor<java.sql.Time,String> {

	@Override
	protected Object cast(Object src) {
		return timeFormat.format(new java.util.Date(((java.sql.Time) src).getTime()));
	}

}
