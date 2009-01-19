package com.zzh.lang.types.castor;

public class Datetime2String extends DateTimeCastor<java.util.Date, String> {

	@Override
	protected String cast(java.util.Date src) {
		return dateTimeFormat.format(src);
	}

}
