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
		assertNull(Strings.removeFirst(null));
		assertEquals("2345", Strings.removeFirst("12345"));
		assertEquals("", Strings.removeFirst(""));
		assertEquals("", Strings.removeFirst("A"));
	}

	@Test
	public void testRemoveFirst2() {
		assertNull(Strings.removeFirst(null, 'A'));
		assertEquals("BCD", Strings.removeFirst("ABCD", 'A'));
		assertEquals("", Strings.removeFirst("", 'A'));
		assertEquals("", Strings.removeFirst("A", 'A'));
		assertEquals("ABCD", Strings.removeFirst("ABCD", 'B'));
	}
}
