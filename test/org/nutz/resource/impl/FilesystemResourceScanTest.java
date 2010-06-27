package org.nutz.resource.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class FilesystemResourceScanTest {

	@Test
	public void testCanWork() {
		assertTrue(new FilesystemResourceScan().canWork());
	}

	@Test
	public void testList() {
		assertTrue(new FilesystemResourceScan().list("src", ".java").size() == 0);
	}

}
