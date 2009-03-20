package com.zzh.lang.random;

import com.zzh.lang.Lang;
import com.zzh.lang.random.ArrayRandom;
import com.zzh.lang.random.Random;

import junit.framework.TestCase;

public class ArrayRandomTest extends TestCase {

	public void testString() {
		Random<String> r = new ArrayRandom<String>(Lang.array("A", "B", "C"));
		int i = 0;
		while (null != r.next()) {
			i++;
		}
		assertEquals(3, i);
	}

}
