package org.nutz.lang.stream;

import static org.junit.Assert.assertEquals;

import java.io.OutputStream;

import org.junit.Test;

public class StringOutputStreamTest {

	@Test
	public void testWriteInt() throws Exception {
		String src = "测试中文";
		StringBuilder sb = new StringBuilder();
		OutputStream stream = new StringOutputStream(sb);
		stream.write(src.getBytes());
		stream.flush();
		String desc = sb.toString();
		assertEquals(src, desc);
	}

}
