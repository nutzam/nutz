package org.nutz.mvc.adaptor;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.NutServlet;

public class JsonAdaptorTest extends AbstractMvcTest{

	@Test
	public void testAdapt() throws ServletException, IOException {
		request.setPathInfo("/json/hello.nut");
		request.setCharacterEncoding("UTF-8");
		request.setInputStream(Mock.servlet.ins(Lang.ins("{pet : {name:'测试'}}")));
		response.setCharacterEncoding("UTF-8");
		((NutServlet)servlet).service(request, response);
		System.out.println(Lang.readAll(new InputStreamReader(Lang.ins("{pet : {name:'测试'}}"))));
		assertEquals("\"!!测试!!\"",response.getContentAsString());//报错? Why?
	}

	@Override
	protected void initServletConfig() {
		servletConfig.addInitParameter("modules", "org.nutz.mvc.adaptor.meta.BaseModule");
	}

}
