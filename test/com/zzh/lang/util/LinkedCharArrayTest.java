package com.zzh.lang.util;

import junit.framework.TestCase;

public class LinkedCharArrayTest extends TestCase {

	private LinkedCharArray lia;

	@Override
	protected void setUp() throws Exception {
		lia = new LinkedCharArray(2);
		lia.push("ABCDEFG");
	}

	public void testNormal() {
		assertEquals(7, lia.size());
		assertEquals('A', lia.first());
		assertEquals('G', lia.last());
	}

	public void testPopfirst() {
		assertEquals('A', lia.popFirst());
		assertEquals('B', lia.popFirst());
		assertEquals('C', lia.popFirst());
		assertEquals('D', lia.popFirst());
		assertEquals('E', lia.popFirst());
		assertEquals('F', lia.popFirst());
		assertEquals('G', lia.popFirst());
		assertEquals(0, lia.popFirst());
		assertEquals(0, lia.popFirst());
		assertEquals(0, lia.size());
	}

	public void testPoplast() {
		assertEquals('G', lia.popLast());
		assertEquals('F', lia.popLast());
		assertEquals('E', lia.popLast());
		assertEquals('D', lia.popLast());
		assertEquals('C', lia.popLast());
		assertEquals('B', lia.popLast());
		assertEquals('A', lia.popLast());
		assertEquals(0, lia.popLast());
		assertEquals(0, lia.popLast());
		assertEquals(0, lia.size());
	}

	public void testPop() {
		lia.popFirst();
		assertEquals('B', lia.first());
		lia.popFirst();
		assertEquals('C', lia.first());
		lia.popLast();
		assertEquals('F', lia.last());
		lia.popLast();
		assertEquals('E', lia.last());
	}

	public void testToString() {
		assertEquals("ABCDEFG", lia.toString());
	}

	public void testGetSet() {
		assertEquals('C', lia.get(2));
		lia.set(2, '$');
		assertEquals('$', lia.get(2));
	}

	public void testGetSetOutOfBound() {
		try {
			lia.get(-1);
			fail();
		} catch (Exception e) {}
		try {
			lia.get(lia.size());
			fail();
		} catch (Exception e) {}
		try {
			lia.set(-1, '#');
			fail();
		} catch (Exception e) {}
		try {
			lia.set(lia.size(), '#');
			fail();
		} catch (Exception e) {}

	}

	public void testClear() {
		assertFalse(lia.isEmpty());
		lia.clear();
		assertTrue(lia.isEmpty());
		assertEquals(0, lia.size());
	}
}
