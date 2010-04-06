package org.nutz.mvc.init;

import org.junit.After;
import org.junit.Before;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockHttpServletResponse;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.NutServlet;

public abstract class NutServletTest {
	
	protected NutServlet nutServlet;
	
	protected MockHttpServletRequest request;
	
	protected MockHttpServletResponse response;
	
	protected MockHttpSession session;
	
	protected MockServletContext servletContext;
	
	protected MockServletConfig servletConfig;
	
	@Before
	public void init() throws Throwable{
		servletContext = new MockServletContext();
		servletConfig = new MockServletConfig(servletContext,"nutz");
		initServletConfig();
		nutServlet = new NutServlet();
		nutServlet.init(servletConfig);

		session = new MockHttpSession(servletContext);
		request = new MockHttpServletRequest(servletContext);
		request.setSession(session);
		response = new MockHttpServletResponse();
	}
	
	protected abstract void initServletConfig();
	
	@After
	public void destroy(){
		if (nutServlet != null )
			nutServlet.destroy();
	}

}
