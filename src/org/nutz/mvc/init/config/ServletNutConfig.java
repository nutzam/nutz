package org.nutz.mvc.init.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.nutz.resource.Scans;

public class ServletNutConfig extends AbstractNutConfig {

	private ServletConfig config;

	public ServletNutConfig(ServletConfig config) {
		this.config = config;
		Scans.me().init(config.getServletContext());
	}

	public ServletContext getServletContext() {
		return config.getServletContext();
	}

	public String getInitParameter(String name) {
		return config.getInitParameter(name);
	}

	public String getAppName() {
		return config.getServletName();
	}

}
