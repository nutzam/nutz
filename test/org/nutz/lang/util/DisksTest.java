package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DisksTest {

	@Test
	public void test_get_canonical_path() {
		assertEquals("B", Disks.getCanonicalPath("A/../B"));
	}

	@Test
	public void test_get_relative_path() {
		assertEquals("../abc.gif", Disks.getRelativePath("D:/dir/sub/uu.txt", "D:/dir/abc.gif"));
	}

}
