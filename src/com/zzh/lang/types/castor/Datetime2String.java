package com.zzh.lang.types.castor;

import java.util.Date;

public class Datetime2String extends DateTimeCastor<java.util.Date,String> {

	@Override
	protected String cast(java.util.Date src) {
		return dateTimeFormat.format((Date) src);
	}

}
