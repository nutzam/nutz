package org.nutz.mvc.init.config;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.nutz.mvc.init.AtMap;
import org.nutz.resource.Scans;

public class FilterNutConfig extends AbstractNutConfig {

	private FilterConfig config;

	public FilterNutConfig(FilterConfig config) {
		this.config = config;
		config.getServletContext().setAttribute(AtMap.class.getName(), new AtMap());
		Scans.me().init(config.getServletContext());
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
