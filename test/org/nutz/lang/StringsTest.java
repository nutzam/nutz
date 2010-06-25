package org.nutz.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringsTest {
	@Test
	public void test_is_empty() {
		assertTrue(Strings.isEmpty(null));
		assertTrue(Strings.isEmpty(""));
		assertFalse(Strings.isEmpty("  "));
		assertFalse(Strings.isEmpty(" at "));
		assertFalse(Strings.isEmpty(new StringBuffer(" ")));
	}
	
	@Test
	public void test_is_blank() {
		assertTrue(Strings.isBlank(null));
		assertTrue(Strings.isBlank(""));
		assertTrue(Strings.isBlank("  "));
		assertFalse(Strings.isBlank(" at "));
		assertTrue(Strings.isBlank(new StringBuffer(" ")));
	}
	
	@Test
	public void test_trim() {
		assertEquals(null, Strings.trim(null));
		assertEquals("", Strings.trim(""));
		assertEquals("", Strings.trim(new StringBuffer(" ")));
		assertEquals("left", Strings.trim("left   "));
		assertEquals("right", Strings.trim("  right"));
		assertEquals("middle", Strings.trim(" middle   "));
		assertEquals("multi world", Strings.trim(" multi world "));
		assertEquals("nutz加油", Strings.trim(" nutz加油 "));
		assertEquals("", Strings.trim(new StringBuffer("    ")));
		assertEquals("multi world", Strings.trim(new StringBuffer("multi world")));
		assertEquals("multi world", Strings.trim(new StringBuffer(" multi world ")));
		assertEquals("nutz加油", Strings.trim(new StringBuilder(" nutz加油 ")));
	}

	@Test
	public void test_is_quote_by_ignore_blank() {
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
	public void test_is_quote_by() {
		assertTrue(Strings.isQuoteBy("[AB]", '[', ']'));

		assertFalse(Strings.isQuoteBy(null, '[', ']'));
		assertFalse(Strings.isQuoteBy("   ", '[', ']'));
		assertFalse(Strings.isQuoteBy("", '[', ']'));
		assertFalse(Strings.isQuoteBy("[", '[', ']'));
		assertFalse(Strings.isQuoteBy("[AB", '[', ']'));
		assertFalse(Strings.isQuoteBy("AB]", '[', ']'));
		assertFalse(Strings.isQuoteBy("  [AB]  ", '[', ']'));
	}

	@Test
	public void test_remove_first() {
		assertNull(Strings.removeFirst(null));
		assertEquals("2345", Strings.removeFirst("12345"));
		assertEquals("", Strings.removeFirst(""));
		assertEquals("", Strings.removeFirst("A"));
	}

	@Test
	public void test_remove_first2() {
		assertNull(Strings.removeFirst(null, 'A'));
		assertEquals("BCD", Strings.removeFirst("ABCD", 'A'));
		assertEquals("", Strings.removeFirst("", 'A'));
		assertEquals("", Strings.removeFirst("A", 'A'));
		assertEquals("ABCD", Strings.removeFirst("ABCD", 'B'));
	}

	@Test
	public void test_upper_word() {
		assertEquals("", Strings.upperWord("-", '-'));
		assertEquals("", Strings.upperWord("---", '-'));
		assertEquals("aBCD", Strings.upperWord("a-b-c-d", '-'));
		assertEquals("helloWorld", Strings.upperWord("hello-world", '-'));
	}

	@Test
	public void test_lower_word() {
		assertEquals("", Strings.lowerFirst(""));
		assertEquals("aCV", Strings.lowerFirst("aCV"));
		assertEquals("eee", Strings.lowerFirst("eee"));
		assertEquals("vCD", Strings.lowerFirst("VCD"));
		assertEquals("vff", Strings.lowerFirst("Vff"));
	}

	@Test
	public void test_split_ignore_blank() {
		assertArrayEquals(null, Strings.splitIgnoreBlank(null));
		assertArrayEquals(new String[]{}, Strings.splitIgnoreBlank(" "));
		assertArrayEquals(new String[]{"2", "3", "5"}, Strings.splitIgnoreBlank("2,3,, 5"));
		assertArrayEquals(new String[]{"2", "3", "5", "6"}, Strings.splitIgnoreBlank("2,3,, 5,6,"));
		assertArrayEquals(new String[]{"2,3,5,6,"}, Strings.splitIgnoreBlank("2,3,5,6,", ",,"));
		assertArrayEquals(new String[]{"2,3", "5", "6,"}, Strings.splitIgnoreBlank("2,3 ,,5,,6,", ",,"));
		assertArrayEquals(new String[]{"2,3", "5", "6,"}, Strings.splitIgnoreBlank("2,3,,5 ,,,,6,", ",,"));
	}
}
