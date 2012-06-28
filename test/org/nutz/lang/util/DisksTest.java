package org.nutz.lang.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.nutz.lang.Files;

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
        String path = Disks.getRelativePath("D:/dir/sub/uu.txt", "D:/dir/abc.gif");
        assertEquals("../abc.gif", path);

        path = Disks.getRelativePath("D:/dir/sub/../uu.txt", "D:/dir/abc.gif");
        assertEquals("abc.gif", path);

        path = Disks.getRelativePath("D:/dir/sub/../sub/uu.txt", "D:/abc.gif");
        assertEquals("../../abc.gif", path);

        path = Disks.getRelativePath("D:/uu.txt", "D:/abc.gif");
        assertEquals("abc.gif", path);
    }

    @Test
    public void test_simple_relative_path() {
        File d1 = Files.findFile("org/nutz/lang");
        File d2 = Files.findFile("org/nutz/json");

        String path = Disks.getRelativePath(d1, d2);
        assertEquals("../json", path);

        d1 = Files.findFile("org/nutz/lang");
        d2 = Files.findFile("org/nutz/lang");

        path = Disks.getRelativePath(d1, d2);
        assertEquals("./", path);

        d1 = Files.findFile("org/nutz/lang");
        d2 = Files.findFile("org/nutz/lang/util");

        path = Disks.getRelativePath(d1, d2);
        assertEquals("util", path);

        d1 = Files.findFile("org/nutz/dao");
        d2 = Files.findFile("org/nutz/lang/util");

        path = Disks.getRelativePath(d1, d2);
        assertEquals("../lang/util", path);
    }

}
