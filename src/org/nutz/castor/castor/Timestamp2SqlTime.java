package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Timestamp2SqlTime extends Castor<Timestamp, Time> {

	@Override
	public Time cast(Timestamp src, Type toType, String... args)
			throws FailToCastObjectException {
		return new Time(src.getTime());
	}

}
