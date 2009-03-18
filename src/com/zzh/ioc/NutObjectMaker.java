package com.zzh.ioc;

import java.util.Map;

public class NutObjectMaker extends ObjectMaker<Nut> {

	private Nut nut;

	public NutObjectMaker(Nut nut) {
		this.nut = nut;
	}

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("$nut");
	}

	@Override
	protected Nut make(Map<String, Object> properties) {
		return nut;
	}

}
