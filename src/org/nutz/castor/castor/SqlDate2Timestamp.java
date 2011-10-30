package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class SqlDate2Timestamp extends Castor<Date, Timestamp> {

	@Override
	public Timestamp cast(Date src, Type toType, String... args)
			throws FailToCastObjectException {
		return new Timestamp(src.getTime());
	}

}
