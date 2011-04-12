package org.nutz.mvc.testapp;

import static org.junit.Assert.*;

import org.junit.Test;

public class BaseTest extends BaseWebappTest{

	
	@Test
	public void test_base(){
		resp = get("/base.jsp");
		assertNotNull(resp);
		assertEquals(200, resp.getStatus());
		assertEquals(getContextPath(), resp.getContent());
	}
	
}
