package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Calendar;

import org.nutz.castor.Castor;

public class Timestamp2Calendar extends Castor<Timestamp, Calendar> {

	@Override
	public Calendar cast(Timestamp src, Type toType, String... args) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(src.getTime());
		return c;
	}

}
