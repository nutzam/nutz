package org.nutz.lang;

import static org.junit.Assert.*;

import org.junit.Test;


public class FilesTest {

	@Test
	public void test_get_major_name(){
		assertEquals("abc",Files.getMajorName("abc.txt"));
		assertEquals("abc",Files.getMajorName("/a/b/c/abc.txt"));
		assertEquals("abc",Files.getMajorName("/a/b/c/abc"));
		assertEquals("",Files.getMajorName(""));
		assertEquals("abc",Files.getMajorName("abc"));
	}
	
}
