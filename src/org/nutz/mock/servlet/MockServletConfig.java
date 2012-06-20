package org.nutz.mock.servlet;

import javax.servlet.ServletContext;

import org.nutz.http.impl.servlet.NutHttpConfig;

/**
 * 模拟ServletConfig
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MockServletConfig extends NutHttpConfig {
	
	public MockServletConfig(ServletContext ctx , String name) {
		this.ctx = ctx;
		this.name = name;
	}

	public void setServletName(String servletName) {
		this.name = servletName;
	}

	public void addInitParameter(String key, String value) {
		this.initParams.put(key, value);
	}
}
