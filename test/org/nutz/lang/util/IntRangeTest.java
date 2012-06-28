package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class IntRangeTest {

    @Test
    public void testMakeString() {
        IntRange r = IntRange.make("3,5");
        assertTrue(r.in(4));
        assertTrue(r.on(3));
        assertTrue(r.on(5));
        assertTrue(r.inon(3));
        assertTrue(r.inon(4));
        assertTrue(r.inon(5));
        assertTrue(r.gt(2));
        assertTrue(r.lt(6));

        assertFalse(r.inon(2));
        assertFalse(r.inon(6));
    }

    @Test
    public void testMakeSimple() {
        IntRange r = IntRange.make("3");
        assertEquals(0, r.getLeft());
        assertEquals(3, r.getRight());
    }
}
