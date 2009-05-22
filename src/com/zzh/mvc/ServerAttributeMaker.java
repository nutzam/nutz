package com.zzh.mvc;

import java.util.Map;

import javax.servlet.ServletContext;

import com.zzh.ioc.ObjectMaker;

public class ServerAttributeMaker extends ObjectMaker {

	private ServletContext context;

	public ServerAttributeMaker(ServletContext context) {
		this.context = context;
	}

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("server");
	}

	@Override
	protected Object make(Map<String, Object> properties) {
		return context.getAttribute(properties.get("server").toString());
	}
}