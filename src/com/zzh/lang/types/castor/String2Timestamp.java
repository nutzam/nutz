package com.zzh.lang.types.castor;

import java.sql.Timestamp;
import java.text.ParseException;

import com.zzh.lang.Lang;

public class String2Timestamp extends DateTimeCastor<String,Timestamp> {

	@Override
	protected Timestamp cast(String src) {
		try {
			return new java.sql.Timestamp(dateTimeFormat.parse((String) src).getTime());
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
