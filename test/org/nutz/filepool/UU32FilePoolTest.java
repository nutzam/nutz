package org.nutz.filepool;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @Author: Haimming
 * @Date: 2020-01-16 15:58
 * @Version 1.0
 */
public class UU32FilePoolTest {

    @Test
    public void creatPoolTest() {
        UU32FilePool pool =new  UU32FilePool(System.getProperty("java.io.tmpdir")+"/UU32FilePool");
        pool.clear();
        File file =pool.createFile("testFile");
        assertNotNull(file);
        assertNotNull(file.getPath());
//        System.out.println(pool.getFileId(file));

    }
}