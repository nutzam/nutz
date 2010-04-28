package org.nutz.mvc.init;

import static junit.framework.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.nutz.mvc.NutServlet;

public class MvcBaseTest extends NutServletTest {

	@Override
	protected void initServletConfig() {
		servletConfig.addInitParameter("modules", "org.nutz.mvc.init.module.MainModule");
	}

	@Test
	public void testIsOK() throws Throwable {
		Method method = NutServlet.class.getDeclaredMethod("isOk");
		method.setAccessible(true);
		assertTrue((Boolean) method.invoke(nutServlet));

		request.setPathInfo("/base/login.nut");
		nutServlet.service(request, response);
		assertEquals("true", response.getContentAsString());
	}

	@Test
	public void testAnotherModule() throws Throwable {
		request.setPathInfo("/two/say");
		nutServlet.service(request, response);
		assertEquals("\"haha\"", response.getContentAsString());
	}
}
