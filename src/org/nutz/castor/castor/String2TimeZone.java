package org.nutz.castor.castor;

import java.util.TimeZone;

import org.nutz.castor.Castor;

public class String2TimeZone extends Castor<String, TimeZone> {

	@Override
	public TimeZone cast(String src, Class<?> toType, String... args) {
		return TimeZone.getTimeZone(src);
	}

}
