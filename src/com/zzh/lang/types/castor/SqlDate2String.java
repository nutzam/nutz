package com.zzh.lang.types.castor;

public class SqlDate2String extends DateTimeCastor<java.sql.Date, String> {

	@Override
	protected String cast(java.sql.Date src) {
		return dateFormat.format(new java.util.Date(src.getTime()));
	}

}
