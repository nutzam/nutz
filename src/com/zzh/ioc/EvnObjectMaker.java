package com.zzh.ioc;

import java.util.Map;

import com.zzh.lang.Strings;

public class EvnObjectMaker extends ObjectMaker<String> {

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("env");
	}

	@Override
	protected String make(Map<String, Object> properties) {
		String envName = Strings.trim(properties.get("env").toString());
		return System.getenv(envName);
	}
}
