package org.nutz.http;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class HttpTest {

	@Test
	public void testGet() {
		Response response = Http.get("http://nutz.googlecode.com");
		assertNotNull(response);
		assertNotNull(response.getContent());
		assertNotNull(response.getDetail());
		assertNotNull(response.getHeader());
		assertNotNull(response.getProtocal());
		assertTrue(response.getStatus() > 0);
		assertNotNull(response.getStream());
	}

	@Ignore
	@Test
	public void testPost() {
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("q", "nutz");
		parms.put("projectsearch", "Search+projects");
		String response = Http.post("http://code.google.com/hosting/search",parms,"utf-8","utf-8");
		assertNotNull(response);
		assertTrue(response.length() > 0);
		assertTrue(response.indexOf("google") > -1);
	}
	
}
