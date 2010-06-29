package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DisksTest {

	@Test
	public void test_get_canonical_path() {
		assertEquals("A", Disks.getCanonicalPath("A/B/.."));
		assertEquals("B", Disks.getCanonicalPath("A/../B"));
		assertEquals("B", Disks.getCanonicalPath("A/B/../../B"));
		assertEquals("B/A", Disks.getCanonicalPath("../B/A"));
		assertEquals("B/A", Disks.getCanonicalPath("../../B/A"));
	}

	@Test
	public void test_get_relative_path() {
		assertEquals("../abc.gif", Disks.getRelativePath("D:/dir/sub/uu.txt", "D:/dir/abc.gif"));
		assertEquals("abc.gif", Disks.getRelativePath("D:/dir/sub/../uu.txt", "D:/dir/abc.gif"));
		assertEquals("../../abc.gif", Disks.getRelativePath("D:/dir/sub/../sub/uu.txt", "D:/abc.gif"));
		assertEquals("abc.gif", Disks.getRelativePath("D:/uu.txt", "D:/abc.gif"));
	}

}
