package org.nutz.lang;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class FilesTest {

	@Test
	public void test_get_major_name() {
		assertEquals("a.b.c", Files.getMajorName("a.b.c.txt"));
		assertEquals("abc", Files.getMajorName("abc.txt"));
		assertEquals("abc", Files.getMajorName("/a/b/c/abc.txt"));
		assertEquals("abc", Files.getMajorName("/a/b/c/abc"));
		assertEquals("", Files.getMajorName(""));
		assertEquals("abc", Files.getMajorName("abc"));
		assertEquals(".abc", Files.getMajorName(".abc"));
		assertEquals(".abc", Files.getMajorName(".abc.txt"));
	}

	@Test
	public void test_rename_suffix() {
		assertEquals("/home/zzh/abc.zdoc", Files.renameSuffix("/home/zzh/abc.txt", ".zdoc"));
		assertEquals("/home/zzh/.zdoc", Files.renameSuffix("/home/zzh/.txt", ".zdoc"));
	}

	@Test
	public void test_find_file_from_home() {
		File f = Files.findFile("~/");
		assertNotNull(f);
	}

	@Test
	public void test_find_file_in_chinese_path() {
		File f = Files.findFile("哈哈/abc.txt");
		assertTrue(f.exists());
	}

}
