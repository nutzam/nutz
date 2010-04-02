package org.nutz.mvc.init;

import org.junit.After;
import org.junit.Before;
import org.nutz.mvc.NutServlet;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

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
		response = new MockHttpServletResponse();
	}
	
	protected abstract void initServletConfig();
	
	@After
	public void destroy(){
		if (nutServlet != null )
			nutServlet.destroy();
	}

}
