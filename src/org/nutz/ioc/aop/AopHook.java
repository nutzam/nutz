package org.nutz.ioc.aop;

import java.util.Map;

public class AopHook {

	public static enum MODE {
		OBJECT_NAME, OBJECT_TYPE
	}

	private String regex;

	private MODE mode;

	private AopHookMethod[] methods;

	private Map<String, Object> config;

	public String getRegex() {
		return regex;
	}

	public MODE getMode() {
		return mode;
	}

	public AopHookMethod[] getMethods() {
		return methods;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

}
