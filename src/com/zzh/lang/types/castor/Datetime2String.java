package com.zzh.lang.types.castor;

import java.util.Date;

public class Datetime2String extends DateTimeCastor<java.util.Date,String> {

	@Override
	protected Object cast(Object src) {
		return dateTimeFormat.format((Date) src);
	}

}
