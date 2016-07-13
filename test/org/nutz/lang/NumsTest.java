package org.nutz.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class NumsTest {

    @Test
    public void test_dimension() {
        assertEquals(99, Nums.dimension("99", 100));
        assertEquals(99, Nums.dimension("99%", 100));
        assertTrue(123.9 == Nums.dimension("123.9%", 100.0));
        assertTrue(123.9 == Nums.dimension("1.239", 100.0));
    }

    @Test
    public void testIsin() {
        assertTrue(Nums.isin(Nums.array(2, 8, 9), 8));
        assertFalse(Nums.isin(Nums.array(2, 8, 9), 12));
    }

    @Test
    public void testJoin() {
        int[] re = Nums.join(Nums.array(4, 8), 5, 9);
        assertEquals(4, re.length);
        assertEquals(4, re[0]);
        assertEquals(8, re[1]);
        assertEquals(5, re[2]);
        assertEquals(9, re[3]);
    }

}
