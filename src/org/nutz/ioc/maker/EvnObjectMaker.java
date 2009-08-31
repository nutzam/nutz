package org.nutz.ioc.maker;

import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.meta.Val;
import org.nutz.lang.Strings;

public class EvnObjectMaker implements ValueMaker {

	public String forType() {
		return Val.env;
	}

	public Object make(Val val) {
		String envName = Strings.trim(val.getValue().toString());
		return System.getenv(envName);
	}

}
