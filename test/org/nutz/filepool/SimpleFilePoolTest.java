package org.nutz.filepool;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @Author: Haimming
 * @Date: 2020-01-16 15:19
 * @Version 1.0
 */
public class SimpleFilePoolTest {

    @Test
    public void hasFile() {
        SimpleFilePool simpleFilePool =new SimpleFilePool(System.getProperty("java.io.tmpdir")+"/simpleFilePool",6);
        simpleFilePool.clear();
        assertEquals(0,simpleFilePool.current());
        simpleFilePool.createDir();
        assertEquals(1,simpleFilePool.current());
        File file =simpleFilePool.createFile("testFile");
        assertNotNull(file);
        assertNotNull(file.getPath());
        assertEquals(2,simpleFilePool.current());
//        long id =simpleFilePool.getFileId(file);
//        System.out.println(id);
//        simpleFilePool.hasFile(id,file.getPath());

    }
}