package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import org.nutz.castor.Castor;

public class Number2Timestamp extends Castor<Number, Timestamp> {

	@Override
	public Timestamp cast(Number src, Type toType, String... args) {
		return new Timestamp(src.longValue());
	}

}
