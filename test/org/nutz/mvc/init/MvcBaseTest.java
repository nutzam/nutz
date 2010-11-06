package org.nutz.mvc.init;

import static junit.framework.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.nutz.mvc.NutServlet;
import org.nutz.mvc.AbstractMvcTest;

public class MvcBaseTest extends AbstractMvcTest {

	@Override
	protected void initServletConfig() {
		servletConfig.addInitParameter("modules", "org.nutz.mvc.init.module.MainModule");
	}

	@Test
	public void testIsOK() throws Throwable {
		Method method = NutServlet.class.getDeclaredMethod("isOk");
		method.setAccessible(true);
		assertTrue((Boolean) method.invoke((NutServlet)servlet));

		request.setPathInfo("/base/login.nut");
		((NutServlet)servlet).service(request, response);
		assertEquals("true", response.getAsString());
	}

	@Test
	public void testAnotherModule() throws Throwable {
		request.setPathInfo("/two/say");
		((NutServlet)servlet).service(request, response);
		assertEquals("\"haha\"", response.getAsString());
	}

}
