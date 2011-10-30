package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Calendar;

import org.nutz.castor.Castor;

public class Number2Calendar extends Castor<Number, Calendar> {

	@Override
	public Calendar cast(Number src, Type toType, String... args) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(src.longValue());
		return c;
	}

}
