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

}
