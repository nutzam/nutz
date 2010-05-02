package org.nutz.mvc.adaptor;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.mock.Mock;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.NutServlet;

public class JsonAdaptorTest extends AbstractMvcTest{

	@Test
	public void testAdapt() throws ServletException, IOException {
		request.setPathInfo("/json/hello.nut");
		request.setInputStream(Mock.servlet.ins(new StringInputStream("{pet : {name:'测试'}}","UTF-8")));
		((NutServlet)servlet).service(request, response);
		assertEquals("\"!!测试!!\"",response.getContentAsString());
	}

	@Override
	protected void initServletConfig() {
		servletConfig.addInitParameter("modules", "org.nutz.mvc.adaptor.meta.BaseModule");
	}

}
