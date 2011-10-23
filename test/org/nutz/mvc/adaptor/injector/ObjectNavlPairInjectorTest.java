package org.nutz.mvc.adaptor.injector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;

public class ObjectNavlPairInjectorTest {

	public static ObjectNavlPairInjector inj(Class<?> type) {
		return new ObjectNavlPairInjector("", type);
	}

	public static ObjectNavlPairInjector inj() {
		return inj(MvcTestPojo.class);
	}

	/**
	 * 根据 Issue 272，如果为空串，原生类型的外覆类应该返回 null
	 */
	@Test
	public void test_balnk_param_to_number() {
		// 准备数据
		MockHttpServletRequest req = Mock.servlet.request();
		req.setParameter("longValue", "  ");
		req.setParameter("num", "  ");

		// 执行
		MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

		// 检测
		assertNull(pojo.longValue);
		assertEquals(0, pojo.num);
	}

	/**
	 * 这个测试将检验在 HTTP 请求中，如果存在多个参数同名的情况，本注入器能否正确处理
	 */
	@Test
	public void test_duplicated_name_params() {
		// 准备数据
		MockHttpServletRequest req = Mock.servlet.request();
		req.setParameter("num", 23);
		req.setParameterValues("names", Lang.array("A", "B", "C"));

		// 执行
		MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

		// 检测
		assertNull(pojo.longValue);
		assertEquals(23, pojo.num);
		assertEquals(3, pojo.names.length);
		assertEquals("A", pojo.names[0]);
		assertEquals("B", pojo.names[1]);
		assertEquals("C", pojo.names[2]);
	}

	@Test
	public void test_array_to_string() {
		// 准备数据
		MockHttpServletRequest req = Mock.servlet.request();
		req.setParameterValues("str", Lang.array("A", "B", "C"));

		// 执行
		MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

		// 检测
		assertEquals("A,B,C", pojo.str);
	}

	@Test
	public void test_string_to_string() {
		// 准备数据
		MockHttpServletRequest req = Mock.servlet.request();
		req.setParameterValues("str", Lang.array("A"));

		// 执行
		MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

		// 检测
		assertEquals("A", pojo.str);
	}
	
	@Test
	public void test_date(){
		// 准备数据
		MockHttpServletRequest req = Mock.servlet.request();
		req.setParameter("date", "2010-01-01");
		// 执行
		MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(sdf.format(pojo.date));
		assertEquals("2010-01-01", sdf.format(pojo.date));
		
		req.setParameter("date", "");
		// 执行
		MvcTestPojo pojoNull = (MvcTestPojo) inj().get(null, req, null, null);
		
		assertEquals(null, pojoNull.date);
	}
	
	@Test
	public void testList(){
	    //准备数据
	    MockHttpServletRequest req = Mock.servlet.request();
	    req.setParameter("books[1]", "a");
	    req.setParameter("books[ads]", "b");
	    req.setParameter("books[3]", "c");
	   
	    //执行
	    MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
	    
	    assertTrue(pojo.books.contains("a"));
	    assertTrue(pojo.books.contains("b"));
	    assertTrue(pojo.books.contains("c"));
	}
	
	@Test
	public void testMap(){
	    //准备数据
	    MockHttpServletRequest req = Mock.servlet.request();
	    req.setParameter("maps[abc].str", "a");
	    req.setParameter("maps[1].str", "b");
	    req.setParameter("maps.jk.str", "c");
	    req.setParameter("maps.jk.maps.nutz", "k");
	    //执行
	    MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
	    
	    assertEquals(pojo.maps.get("abc").str, "a");
	    assertEquals(pojo.maps.get("1").str, "b");
	    assertEquals(pojo.maps.get("jk").str, "c");
	    assertEquals(pojo.maps.get("jk").maps.get("nutz"), "k");
	}
	
	@Test
	public void testSet(){
	    //准备数据
	    MockHttpServletRequest req = Mock.servlet.request();
	    req.setParameter("sets.jk.str", "c");
	    req.setParameter("sets.jk.maps.nutz", "k");
	    //执行
	    MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
	    
	    for(MvcTestPojo m : pojo.sets){
	        assertEquals(m.str, "c");
	        assertEquals(m.maps.get("nutz"), "k");
	    }
	}

}
