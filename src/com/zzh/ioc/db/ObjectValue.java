package com.zzh.ioc.db;

import com.zzh.ioc.Value;

public class ObjectValue implements Value {

	ObjectValue() {
	}

	String referName;

	String value;

	@Override
	public String getReferName() {
		return referName;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
