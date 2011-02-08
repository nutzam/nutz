package org.nutz.mvc.init;

import static junit.framework.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutServlet;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.RequestPath;

public class MvcBaseTest extends AbstractMvcTest {

	@Override
	protected void initServletConfig() {
		servletConfig.addInitParameter("modules", "org.nutz.mvc.init.module.MainModule");
	}

	@Test
	public void testIsOK() throws Throwable {
		Method method = NutServlet.class.getDeclaredMethod("isOk");
		method.setAccessible(true);
		assertTrue((Boolean) method.invoke(servlet));

		request.setPathInfo("/base/login.nut");
		servlet.service(request, response);
		assertEquals("true", response.getAsString());
	}

	@Test
	public void testAnotherModule() throws Throwable {
		request.setPathInfo("/two/say");
		servlet.service(request, response);
		assertEquals("\"haha\"", response.getAsString());
	}

	@Test
	public void testPointPath() throws Throwable {
		request.setPathInfo("/1.2/say.nutz");
		RequestPath path = Mvcs.getRequestPathObject(request);
		assertNotNull(path);
		assertEquals("/1.2/say", path.getPath());
		assertEquals("nutz", path.getSuffix());
		
		request.setPathInfo("/1.2/say");
		path = Mvcs.getRequestPathObject(request);
		assertNotNull(path);
		assertEquals("/1.2/say", path.getPath());
		
		request.setPathInfo("/1.2/say.po/");
		path = Mvcs.getRequestPathObject(request);
		assertNotNull(path);
		assertEquals("/1.2/say.po/", path.getPath());
		
		request.setPathInfo("/1.2/say.po/.nut");
		path = Mvcs.getRequestPathObject(request);
		assertNotNull(path);
		assertEquals("/1.2/say.po/", path.getPath());
	}
	
	@Test
	public void testRequestParms_error() throws Throwable {
		request.setPathInfo("/two/login.nutz");
		request.addParameter("username", "wendal");
		request.addParameter("password", "123456");
		request.addParameter("authCode", "Nutz");
		servlet.service(request, response);
		assertTrue(response.getAsString().indexOf("NumberFormatException") > -1);
	}
	
	@Test
	public void testRequestParms() throws Throwable {
		request.setPathInfo("/two/login.nutz");
		request.addParameter("username", "wendal");
		request.addParameter("password", "123456");
		request.addParameter("authCode", "236475");
		servlet.service(request, response);
		assertEquals("true", response.getAsString());
	}
}
