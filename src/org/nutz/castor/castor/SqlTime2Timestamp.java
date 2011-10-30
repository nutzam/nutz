package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class SqlTime2Timestamp extends Castor<Time, Timestamp> {

	@Override
	public Timestamp cast(Time src, Type toType, String... args)
			throws FailToCastObjectException {
		return new Timestamp(src.getTime());
	}

}
