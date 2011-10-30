package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Calendar;

import org.nutz.castor.Castor;

public class Calendar2Long extends Castor<Calendar, Long> {
	@Override
	public Long cast(Calendar src, Type toType, String... args) {
		return src.getTimeInMillis();
	}
}
