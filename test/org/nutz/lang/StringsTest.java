package org.nutz.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringsTest {

	@Test
	public void testIsQuoteByIgnoreBlank() {
		assertTrue(Strings.isQuoteByIgnoreBlank("[AB]", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("[]", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("   []", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("[]   ", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("  [  AB  ]   ", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("  [  AB  ]", '[', ']'));
		assertTrue(Strings.isQuoteByIgnoreBlank("[  AB  ]   ", '[', ']'));

		assertFalse(Strings.isQuoteByIgnoreBlank(null, '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("[AB", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("   [AB", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("AB]", '[', ']'));
		assertFalse(Strings.isQuoteByIgnoreBlank("AB]   ", '[', ']'));

	}

	@Test
	public void testIsQuoteBy() {
		assertTrue(Strings.isQuoteBy("[AB]", '[', ']'));

		assertFalse(Strings.isQuoteBy(null, '[', ']'));
		assertFalse(Strings.isQuoteBy("   ", '[', ']'));
		assertFalse(Strings.isQuoteBy("", '[', ']'));
		assertFalse(Strings.isQuoteBy("[", '[', ']'));
		assertFalse(Strings.isQuoteBy("[AB", '[', ']'));
		assertFalse(Strings.isQuoteBy("AB]", '[', ']'));
	}

	@Test
	public void testRemoveFirst() {

		assertEquals(Strings.removeFirst("12345"), "2345");
		assertEquals(Strings.removeFirst(null), null);
		assertEquals(Strings.removeFirst(""), "");

		assertEquals(Strings.removeFirst("啊中国"), "中国");

	}

	@Test
	public void testRemoveFirst2() {

		assertEquals(Strings.removeFirst("12345", '1'), "2345");
		assertEquals(Strings.removeFirst("12345", ' '), "12345");
		assertEquals(Strings.removeFirst("12345", '5'), "12345");

		assertEquals(Strings.removeFirst(null, '1'), null);
		assertEquals(Strings.removeFirst("", '1'), "");

		assertEquals(Strings.removeFirst("啊中国", '啊'), "中国");

	}
}
