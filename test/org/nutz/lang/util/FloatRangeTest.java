package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class FloatRangeTest {

    @Test
    public void testMakeString() {
        FloatRange r = FloatRange.make(".3 : .5");
        assertTrue(r.in(.4f));
        assertTrue(r.on(.3f));
        assertTrue(r.on(.5f));
        assertTrue(r.inon(.3f));
        assertTrue(r.inon(.4f));
        assertTrue(r.inon(.5f));
        assertTrue(r.gt(.2f));
        assertTrue(r.lt(.6f));

        assertFalse(r.inon(2));
        assertFalse(r.inon(6));
    }

    @Test
    public void testMakeSimple() {
        FloatRange r = FloatRange.make("3");
        assertTrue(0f == r.getLeft());
        assertTrue(3f == r.getRight());
    }

}
