package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import org.nutz.castor.Castor;

public class Timestamp2Long extends Castor<Timestamp, Long> {

	@Override
	public Long cast(Timestamp src, Type toType, String... args) {
		return src.getTime();
	}

}
