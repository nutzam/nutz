package org.nutz.castor.castor;

import java.util.TimeZone;

import org.nutz.castor.Castor;

public class String2TimeZone extends Castor<String, TimeZone> {

	@Override
	protected TimeZone cast(String src, Class<?> toType, String... args) {
		return TimeZone.getTimeZone(src);
	}

}
