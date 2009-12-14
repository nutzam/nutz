package org.nutz.castor;

import java.text.SimpleDateFormat;

import org.nutz.castor.castor.DateTimeCastor;

class DefaultCastorSetting {

	public static void setup(DateTimeCastor<?, ?> c) {
		c.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		c.setTimeFormat(new SimpleDateFormat("HH:mm:ss"));
		c.setDateTimeFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

}
