package org.nutz.mvc.config;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.nutz.resource.Scans;

public class ServletNutConfig extends AbstractNutConfig {

	private ServletConfig config;

	public ServletNutConfig(ServletConfig config) {
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

	public List<String> getInitParameterNames() {
		return enum2list(config.getInitParameterNames());
	}

	public String getAppName() {
		return config.getServletName();
	}

}
