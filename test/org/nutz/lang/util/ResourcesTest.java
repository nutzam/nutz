package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourcesTest {

	@Test
	public void test_get_jar_path() {
		String jarPath = "F:\\folder\\a.jar!\\org\\nutz\\castor\\castor";
		assertEquals("F:\\folder\\a.jar", Resources.getJarPath(jarPath));

		jarPath = "file:\\F:\\folder\\a.jar!\\org\\nutz\\castor\\castor";
		assertEquals("F:\\folder\\a.jar", Resources.getJarPath(jarPath));
		
		jarPath = "file:/home/me/a.jar!\\org\\nutz\\castor\\castor";
		assertEquals("/home/me/a.jar", Resources.getJarPath(jarPath));
		
		jarPath = "/home/me/a.jar!\\org\\nutz\\castor\\castor";
		assertEquals("/home/me/a.jar", Resources.getJarPath(jarPath));
	}

}
