package com.zzh.lang.random;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zzh.lang.Lang;
import com.zzh.lang.random.ArrayRandom;
import com.zzh.lang.random.Random;

public class ArrayRandomTest {

	@Test
	public void testString() {
		Random<String> r = new ArrayRandom<String>(Lang.array("A", "B", "C"));
		int i = 0;
		while (null != r.next()) {
			i++;
		}
		assertEquals(3, i);
	}

}
