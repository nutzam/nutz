package org.nutz.filepool;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    
    @Test
    public void test_upload_32_threads() throws InterruptedException {
        // 这个测试是验证NutFilePool的线程不安全特性的
        final Set<String> ids = new HashSet<String>();
        ExecutorService es = Executors.newFixedThreadPool(32);
        String home = Disks.normalize("~/tmp_nutz");
        Files.deleteDir(new File(home));
        new File(home).mkdirs();
        final NutFilePool fp = new NutFilePool(home, 1000000);
        final boolean res[] = new boolean[1];
        for (int i = 0; i < 32; i++) {
            es.submit(new Runnable() {
                public void run() {
                    for (int i = 0; i < 128 && !res[0]; i++) {
                        File f = fp.createFile(".dat");
                        boolean re = ids.add(f.getPath());
                        if (!re) {
                            res[0] = true;
                            System.out.println("fuck");
                            break;
                        }
                    }
                }
            });
        }
        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);
        Files.deleteDir(new File(home));
        //assertFalse(res[0]);
    }
}
