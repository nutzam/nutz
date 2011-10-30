package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.Date;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Datetime2SqlDate extends Castor<Date, java.sql.Date> {

	@Override
	public java.sql.Date cast(Date src, Type toType, String... args)
			throws FailToCastObjectException {
		return new java.sql.Date(src.getTime());
	}

}
