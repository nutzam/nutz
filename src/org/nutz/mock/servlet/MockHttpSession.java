package org.nutz.mock.servlet;

import org.nutz.http.server.impl.servlet.NutHttpServletSession;

public class MockHttpSession extends NutHttpServletSession {
	
	public MockHttpSession(MockServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
