package org.nutz.filepool;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class PoolsTest {

    @Test
    public void testGetFileId() {
        File home = new File("/home/zozoh/pool");
        File f = new File("/home/zozoh/pool/00/00/00/00/00/00/00/04");
        assertEquals(4, Pools.getFileId(home, f));
    }

}
