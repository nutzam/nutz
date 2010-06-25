package org.nutz.resource.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class JarResourceScanTest {

	@Test
	public void testList() {
		assertTrue(new JarResourceScan().list("org.junit", ".class").size() > 0);
	}

	@Test
	public void testCanWork(){
		assertTrue(new JarResourceScan().canWork());
	}
}
