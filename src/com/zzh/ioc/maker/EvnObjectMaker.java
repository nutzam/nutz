package com.zzh.ioc.maker;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;
import com.zzh.lang.Strings;

public class EvnObjectMaker implements ValueMaker {

	@Override
	public String forType() {
		return Val.env;
	}

	@Override
	public Object make(Val val) {
		String envName = Strings.trim(val.getValue().toString());
		return System.getenv(envName);
	}

}
