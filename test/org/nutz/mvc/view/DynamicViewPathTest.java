package org.nutz.mvc.view;

import org.junit.Test;
import org.nutz.mvc.AbstractMvcTest;
import static org.junit.Assert.*;


public class DynamicViewPathTest extends AbstractMvcTest{
	protected void initServletConfig() {
		servletConfig.addInitParameter("modules", "org.nutz.mvc.view.redirect.MainModule");
	}
	
	@Test
	public void elTest(){
		request.setParameter("test", "abc");
		DynamicViewPath dvp = new DynamicViewPath(request, null);
		String el = "test";
		assertEquals(dvp.parseEl(el), "abc");
	}

	@Test
	public void simpleTest(){
		request.setParameter("test", "abc");
		request.setParameter("t.name", "123");
		DynamicViewPath dvp = new DynamicViewPath(request, null);
		String path = "/abc/${test}";
		assertEquals(dvp.parsePath(path), "/abc/abc");
		
//		无法从 req 中取出带 . 的字符串变量
//		path = "/abc/${\"t.name\"}/${test}";
//		assertEquals(dvp.parsePath(path), "/abc/123/abc");
	}
	
	@Test
	public void pojoTest(){
		request.setParameter("test", "abc");
		DynamicViewPath dvp = new DynamicViewPath(request, new pojo());
		String path = "/abc/${obj.name}";
		assertEquals(dvp.parsePath(path), "/abc/pojo");
	}
	
	class pojo{
		int id = 1;
		String name="pojo";
	}
}
