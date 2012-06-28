package org.nutz.lang;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

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

    @Test
    public void test_find_file_in_jar() throws ClassNotFoundException, IOException {
        URL url = getClass().getResource("/org/nutz/lang/one.jar");
        assertNotNull(url);
        ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{url});
        InputStream is = Files.findFileAsStream("org/nutz/plugin/Plugin.w",
                                                classLoader.loadClass("org.nutz.lang.XXXX"));
        assertNotNull(is);
        assertEquals(is.available(), 133);
    }

    @Test
    public void test_getParent() {
        assertEquals("/a/b/c", Files.getParent("/a/b/c/d"));
        assertEquals("/", Files.getParent("/"));
        assertEquals("", Files.getParent(""));
        assertNull(Files.getParent(null));
        assertEquals("\\a\\b", Files.getParent("\\a\\b\\c"));
    }

    @Test
    public void test_renamePath() {
        assertEquals("/a/b/c", Files.renamePath("/a/b/fff", "c"));
        assertEquals("\\a\\b/c.txt", Files.renamePath("\\a\\b\\fff", "c.txt"));
        assertEquals("a", Files.renamePath("", "a"));
        assertEquals("a", Files.renamePath(null, "a"));
    }

    @Test
    public void test_get_suffix_name() {
        assertEquals(null, Files.getSuffixName((String) null));
        assertEquals("", Files.getSuffixName(""));
        assertEquals("", Files.getSuffixName("abc."));
        assertEquals("txt", Files.getSuffixName("abc.txt"));
        assertEquals("txt", Files.getSuffixName("abc.bcd.txt"));
    }
}
