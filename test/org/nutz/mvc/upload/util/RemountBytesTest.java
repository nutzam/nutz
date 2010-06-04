package org.nutz.mvc.upload.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Lang;

public class RemountBytesTest {
	
	private static RemountBytes RB(String s) {
		return RemountBytes.create(Lang.toBytes(s.toCharArray()));
	}

	@Test
	public void test_case_A() {
		RemountBytes rb = RB("AABC");
		assertEquals(0,rb.pos[0]);
		assertEquals(0,rb.pos[1]);
		assertEquals(1,rb.pos[2]);
		assertEquals(0,rb.pos[3]);
	}

	@Test
	public void test_case_B() {
		RemountBytes rb = RB("ABABX");
		assertEquals(0,rb.pos[0]);  // A
		assertEquals(0,rb.pos[1]);  // B
		assertEquals(0,rb.pos[2]);  // A
		assertEquals(0,rb.pos[3]);  // B
		assertEquals(2,rb.pos[4]);  // X
	}
	

	@Test
	public void test_case_C() {
		RemountBytes rb = RB("ABCABCD");
		assertEquals(0,rb.pos[0]);  // A
		assertEquals(0,rb.pos[1]);  // B
		assertEquals(0,rb.pos[2]);  // C
		assertEquals(0,rb.pos[3]);  // A
		assertEquals(0,rb.pos[4]);  // B
		assertEquals(0,rb.pos[5]);  // C
		assertEquals(3,rb.pos[6]);  // D
	}

}
