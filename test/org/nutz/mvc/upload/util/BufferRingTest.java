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
		byte[] boundary = Lang.toBytes("---".toCharArray());

		String str = "1234567890ABCDEfgh---A---B------ENDL--";
		InputStream ins = Lang.ins(str);
		String s;
		MarkMode mode;
		RingItem ri;

		BufferRing br = new BufferRing(ins, 3, 5);
		/**
		 * =================================================<br>
		 * 12345 67890 ABCDE
		 */
		br.load();
		assertEquals(15, br.readed);
		mode = br.mark(boundary);
		assertEquals(MarkMode.NOT_FOUND, mode);
		s = read(br);
		assertEquals('1', (char) br.item.buffer[0]);
		assertTrue(br.item.isDone4Mark());
		assertFalse(br.item.isLoaded);
		assertEquals("1234567890ABCDE", s);

		/**
		 * =================================================<br>
		 * fgh-- -A--- B----
		 */
		br.load();
		assertEquals(30, br.readed);
		mode = br.mark(boundary);
		assertEquals(MarkMode.FOUND, mode);
		// ITEM
		ri = br.item;
		assertTrue(ri.isLoaded);
		assertEquals(0, ri.l);
		assertEquals(3, ri.r);
		assertEquals(5, ri.nextmark);
		assertTrue(ri.isDone4Mark());
		// ITEM.NEXT
		ri = br.item.next;
		assertTrue(ri.isLoaded);
		assertEquals(1, ri.l);
		assertEquals(1, ri.r);
		assertEquals(0, ri.nextmark);
		assertFalse(ri.isDone4Mark());
		// ITEM.NEXT.NEXT
		ri = br.item.next.next;
		assertTrue(ri.isLoaded);
		assertEquals(0, ri.l);
		assertEquals(0, ri.r);
		assertEquals(0, ri.nextmark);
		assertFalse(ri.isDone4Mark());

		s = read(br);
		assertEquals("fgh", s);

		/**
		 * =================================================<br>
		 * -A--- B----
		 */
		br.load();
		assertEquals(30, br.readed);
		mode = br.mark(boundary);
		assertEquals(MarkMode.FOUND, mode);
		// ITEM
		ri = br.item;
		assertTrue(ri.isLoaded);
		assertEquals(1, ri.l);
		assertEquals(2, ri.r);
		assertEquals(5, ri.nextmark);
		assertTrue(ri.isDone4Mark());
		// ITEM.NEXT
		ri = br.item.next;
		assertTrue(ri.isLoaded);
		assertEquals(0, ri.l);
		assertEquals(0, ri.r);
		assertEquals(0, ri.nextmark);
		assertFalse(ri.isDone4Mark());
		// ITEM.NEXT.NEXT
		ri = br.item.next.next;
		assertFalse(ri.isLoaded);

		s = read(br);
		assertEquals("A", s);

		/**
		 * =================================================<br>
		 * B----
		 */
		br.load();
		assertEquals(30, br.readed);
		mode = br.mark(boundary);
		assertEquals(MarkMode.FOUND, mode);
		// ITEM
		ri = br.item;
		assertTrue(ri.isLoaded);
		assertEquals(0, ri.l);
		assertEquals(1, ri.r);
		assertEquals(4, ri.nextmark);
		assertFalse(ri.isDone4Mark());
		// ITEM.NEXT
		ri = br.item.next;
		assertFalse(ri.isLoaded);
		// ITEM.NEXT.NEXT
		ri = br.item.next.next;
		assertFalse(ri.isLoaded);

		s = read(br);
		assertEquals("B", s);
		ri = br.item;
		assertTrue(ri.isLoaded);
		assertEquals(4, ri.l);
		assertEquals(4, ri.r);
		assertEquals(4, ri.nextmark);
		assertFalse(ri.isDone4Mark());

		/**
		 * =================================================<br>
		 * B---- --END
		 */
		br.load();
		assertEquals(30, br.readed);
		mode = br.mark(boundary);
		assertEquals(35, br.readed);
		assertEquals(MarkMode.FOUND, mode);
		// ITEM
		ri = br.item;
		assertTrue(ri.isLoaded);
		assertEquals(4, ri.l);
		assertEquals(5, ri.r);
		assertEquals(5, ri.nextmark);
		assertTrue(ri.isDone4Mark());
		// ITEM.NEXT
		ri = br.item.next;
		assertTrue(ri.isLoaded);
		assertEquals(2, ri.l);
		assertEquals(2, ri.r);
		assertEquals(0, ri.nextmark);
		assertFalse(ri.isDone4Mark());
		// ITEM.NEXT.NEXT
		ri = br.item.next.next;
		assertFalse(ri.isLoaded);
		
		s = read(br);
		assertEquals("", s);

		br.load();
		mode = br.mark(boundary);
		assertEquals(MarkMode.STREAM_END, mode);
		s = read(br);
		assertEquals("ENDL--", s);
	}
}
