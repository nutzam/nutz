package org.nutz.mvc;

import javax.servlet.Servlet;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockHttpServletResponse;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.NutServlet;

@Ignore
public abstract class AbstractMvcTest {

	protected Servlet servlet;

	protected MockHttpServletRequest request;

	protected MockHttpServletResponse response;

	protected MockHttpSession session;

	protected MockServletContext servletContext;

	protected MockServletConfig servletConfig;

	@Before
	public void init() throws Throwable {
		servletContext = new MockServletContext();
		servletConfig = new MockServletConfig(servletContext, "nutz");
		initServletConfig();
		servlet = new NutServlet();
		servlet.init(servletConfig);

		session = Mock.servlet.session(servletContext);
		request = Mock.servlet.request().setSession(session);
		request.setSession(session);
		response = new MockHttpServletResponse();
	}

	protected abstract void initServletConfig();

	@After
	public void destroy() {
		if (servlet != null)
			servlet.destroy();
	}

}
