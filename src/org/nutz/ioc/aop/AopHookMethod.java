package org.nutz.ioc.aop;

import java.util.Map;

public class AopHookMethod {

	public static enum ACCESS {
		ALL, PUBLIC, PROTECTED
	}

	private String regex;

	private String ignore;

	private ACCESS access;

	private Map<String, Object> config;

	public String getRegex() {
		return regex;
	}

	public String getIgnore() {
		return ignore;
	}

	public ACCESS getAccess() {
		return access;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

}
