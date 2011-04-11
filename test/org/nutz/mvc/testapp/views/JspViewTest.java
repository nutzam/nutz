package org.nutz.mvc.testapp.views;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.http.Response;
import org.nutz.mvc.testapp.BaseWebappTest;

/**
 * For ViewTestModule
 *
 */
public class JspViewTest extends BaseWebappTest {

	@Test
	public void test_simple(){
		Response resp = get("/views/jsp");
		assertEquals("null", resp.getContent());
	}
}
