package org.nutz.lang.random;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.lang.Lang;
import org.nutz.lang.random.Random;
import org.nutz.lang.random.ListRandom;

public class ListRandomTest {

	@Test
	public void testString() {
		Random<String> r = new ListRandom<String>(Lang.list("A", "B", "C"));
		int i = 0;
		while (null != r.next()) {
			i++;
		}
		assertEquals(3, i);
	}

}