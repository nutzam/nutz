package org.nutz.mvc.init.config;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class FilterNutConfig extends AbstractNutConfig {

	private FilterConfig config;

	public FilterNutConfig(FilterConfig config) {
		this.config = config;
	}

	public ServletContext getServletContext() {
		return config.getServletContext();
	}

	public String getInitParameter(String name) {
		return config.getInitParameter(name);
	}

	public String getAppName() {
		return config.getFilterName();
	}

}
