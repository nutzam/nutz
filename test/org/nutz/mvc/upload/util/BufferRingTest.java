package org.nutz.mvc.upload.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.nutz.lang.Lang;

public class BufferRingTest {

	private static String read(BufferRing br) throws IOException {
		StringBuilder sb = new StringBuilder();
		OutputStream ops = Lang.ops(sb);
		br.dump(ops);
		return sb.toString();
	}

	@Test
	public void test_normal_read() throws IOException {
		byte[] boundary = (byte[]) Lang.array2array("---".toCharArray(),byte.class);
		
		String str = "1234567890ABCDEfgh---A---B------ENDL--";
		InputStream ins = Lang.ins(str);
		String s;
		MarkMode mode;

		BufferRing br = new BufferRing(ins, 3, 5);

		br.load();
		mode = br.mark(boundary);
		assertEquals(MarkMode.NOT_FOUND, mode);
		s = read(br);
		assertEquals("1234567890ABCDE", s);

		br.load();
		mode = br.mark(boundary);
		assertEquals(MarkMode.FOUND, mode);
		s = read(br);
		assertEquals("fgh", s);

		br.load();
		mode = br.mark(boundary);
		assertEquals(MarkMode.FOUND, mode);
		s = read(br);
		assertEquals("A", s);

		br.load();
		mode = br.mark(boundary);
		assertEquals(MarkMode.FOUND, mode);
		s = read(br);
		assertEquals("B", s);

		br.load();
		mode = br.mark(boundary);
		assertEquals(MarkMode.FOUND, mode);
		s = read(br);
		assertEquals("", s);

		br.load();
		mode = br.mark(boundary);
		assertEquals(MarkMode.STREAM_END, mode);
		s = read(br);
		assertEquals("ENDL--", s);
	}

}
