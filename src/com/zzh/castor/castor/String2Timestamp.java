package com.zzh.castor.castor;

import java.sql.Timestamp;
import java.text.ParseException;

import com.zzh.lang.Lang;

public class String2Timestamp extends DateTimeCastor<String, Timestamp> {

	@Override
	protected Timestamp cast(String src, Class<?> toType) {
		try {
			return new java.sql.Timestamp(dateTimeFormat.parse(src).getTime());
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
