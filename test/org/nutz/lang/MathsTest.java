package org.nutz.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathsTest {

	@Test
	public void testIsMask() {
		assertTrue(Maths.isMask(1 << 2, 1 << 1 | 1 << 2));
		assertFalse(Maths.isMask(1 << 2, 1 << 4 | 1 << 1));
	}

	@Test
	public void testIsMaskAll() {
		assertFalse(Maths.isMaskAll(1 << 2, 1 << 1 | 1 << 2));
		assertTrue(Maths.isMaskAll(7 << 1, 1 << 1 | 1 << 2 | 1 << 3));
	}

	@Test
	public void test_extract_int() {
		assertEquals(3, Maths.extract(7, 1, 3));
		assertEquals(1, Maths.extract(7, 0, 1));
		assertEquals(3, Maths.extract(255, 4, 6));
	}

}
