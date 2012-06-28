package org.nutz.lang.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class ByteInputStreamTest {
    @Test
    public void test_read_ByteInputStream() throws IOException {
        byte[] bs_abc = {'a','b','c'};
        ByteInputStream bis = new ByteInputStream(bs_abc);
        assertEquals((int) ('a'), bis.read());
        assertEquals((int) ('b'), bis.read());
        assertEquals((int) ('c'), bis.read());
        assertEquals(-1, bis.read());
        bis.close();
    }
}
