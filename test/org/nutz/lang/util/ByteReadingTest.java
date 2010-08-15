package org.nutz.lang.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ByteReadingTest {
	@Test
	public void test_next_and_prev_operator() {
		int[] bytes_5 = {5, 4, 3, 2, 1};
		ByteReading br = new ByteReading(bytes_5);
		assertEquals(0, br.cursor());
		assertEquals(5, br.size());

		br.setMark(0);
		assertArrayEquals(new int[]{5, 4, 0}, br.next(2).getBytes());
		assertNull(br.next(4));
		assertArrayEquals(new int[]{5, 4, 3, 2, 1, 0}, br.getBytes());
		assertArrayEquals(new int[]{5, 4, 3, 0}, br.prev(2).getBytes());
		assertArrayEquals(new int[]{0}, br.prev(5).getBytes());
	}
}
