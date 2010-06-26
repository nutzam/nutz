package org.nutz.resource.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClasspathResourceScanTest {

	@Test
	public void testCanWork() {
		assertTrue(new FilesystemResourceScan().canWork());
	}

	@Test
	public void testList() {
		assertTrue(new ClasspathResourceScan().list("org/nutz", ".class").size() > 0);
	}

}
