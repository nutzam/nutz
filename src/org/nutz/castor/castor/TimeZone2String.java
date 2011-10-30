package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.TimeZone;

import org.nutz.castor.Castor;

public class TimeZone2String extends Castor<TimeZone, String> {

	@Override
	public String cast(TimeZone src, Type toType, String... args) {
		return src.getID();
	}

}
