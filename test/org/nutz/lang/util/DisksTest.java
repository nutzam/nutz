package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DisksTest {

	@Test
	public void testGetCanonicalPath() {
		assertEquals("B",Disks.getCanonicalPath("A/../B"));
	}

}
