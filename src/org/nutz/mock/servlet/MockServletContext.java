package org.nutz.mock.servlet;

import org.nutz.http.server.impl.servlet.NutServletContext;

public class MockServletContext extends NutServletContext {

	private String servletContextName;

	public String getServletContextName() {
		return servletContextName;
	}

	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}


}
