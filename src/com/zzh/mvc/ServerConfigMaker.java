package com.zzh.mvc;

import java.util.Map;

import javax.servlet.ServletConfig;

import com.zzh.ioc.ObjectMaker;

public class ServerConfigMaker extends ObjectMaker {

	private ServletConfig config;

	public ServerConfigMaker(ServletConfig config) {
		this.config = config;
	}

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("config");
	}

	@Override
	protected String make(Map<String, Object> properties) {
		String name = properties.get("config").toString();
		if ("$server-name".equals(name))
			return config.getServletName();
		return config.getInitParameter(name);
	}
}
