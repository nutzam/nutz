package org.nutz.filepool;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;

public class NutFilePoolTest {

    private FilePool filePool;

    @Before
    public void init() {
        filePool = new NutFilePool("tmp-pool");
    }

    @After
    public void close() throws Throwable {
        filePool.clear();
        Files.deleteDir(new File("tmp-pool"));
    }

    @Test
    public void testHasFile() {
        filePool.createFile("tmp");
        assertTrue(filePool.hasFile(1, "tmp"));
    }

    @Test
    public void testCurrent() {
        filePool.createFile("tmp");
        assertTrue(filePool.current() > 0);
    }

    @Test
    public void testRemoveFile() {
        filePool.createFile("tmp");
        filePool.removeFile(1, "tmp");
        assertFalse(filePool.hasFile(1, "tmp"));
    }

    @Test
    public void testCreateFile() {
        filePool.createFile("tmp");
        assertTrue(filePool.hasFile(1, "tmp"));
    }

    @Test
    public void testGetFileId() {
        File tmp = filePool.createFile("tmp");
        assertEquals(-1, filePool.getFileId(tmp));
    }

    @Test
    public void testClear() {
        File tmp = filePool.createFile("tmp");
        filePool.clear();
        assertTrue(-1 == filePool.getFileId(tmp));
    }

    @Test public void test_blank_suffix(){
        String home = Disks.normalize("~/tmp_nutz");
        new File(home).delete();
        new File(home).mkdirs();
        FilePool filePool = new NutFilePool(home);
        File f = filePool.createFile("");
        System.out.println(f);

        new NutFilePool(home);
        new NutFilePool(home);
        new NutFilePool(home);
        //在生成一次,报错
        new NutFilePool(home);
    }
}
