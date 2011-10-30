package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Date;

import org.nutz.castor.Castor;

public class Datetime2Long extends Castor<java.util.Date, Long> {

	@Override
	public Long cast(Date src, Type toType, String... args) {
		return src.getTime();
	}

}
