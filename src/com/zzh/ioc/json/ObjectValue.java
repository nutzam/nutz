package com.zzh.ioc.json;

import com.zzh.ioc.Value;

public class ObjectValue implements Value {

	ObjectValue() {
	}

	String referName;

	Object value;

	@Override
	public String getReferName() {
		return referName;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
