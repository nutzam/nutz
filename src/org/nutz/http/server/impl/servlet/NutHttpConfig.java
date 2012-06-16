package org.nutz.http.server.impl.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class NutHttpConfig implements ServletConfig, FilterConfig {

	protected String name;
	public String getFilterName() {
		return name;
	}
	public String getServletName() {
		return name;
	}

	protected ServletContext ctx;
	public ServletContext getServletContext() {
		return ctx;
	}

	protected Map<String, String> initParams = new HashMap<String, String>();
	public String getInitParameter(String name) {
		return initParams.get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(initParams.keySet());
	}

}
