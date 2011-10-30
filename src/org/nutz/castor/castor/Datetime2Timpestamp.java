package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Datetime2Timpestamp extends Castor<java.util.Date, Timestamp> {

	@Override
	public Timestamp cast(Date src, Type toType, String... args)
			throws FailToCastObjectException {
		return new Timestamp(src.getTime());
	}

}
