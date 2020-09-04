package org.nutz.lang.random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.lang.Lang;

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