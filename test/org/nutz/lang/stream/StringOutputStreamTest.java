package org.nutz.lang.stream;

import static org.junit.Assert.assertEquals;

import java.io.OutputStream;

import org.junit.Test;
import org.nutz.lang.Streams;

public class StringOutputStreamTest {

    @Test
    public void testWriteInt() throws Exception {
        String src = "测试中文";
        StringBuilder sb = new StringBuilder();
        OutputStream stream = new StringOutputStream(sb);
        stream.write(src.getBytes());
        Streams.safeFlush(stream);
        Streams.safeClose(stream);
        assertEquals(src, sb.toString());
    }

}
