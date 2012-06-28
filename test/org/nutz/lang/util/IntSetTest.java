package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class IntSetTest {

    @Test
    public void test_inon() {
        IntSet irs = IntSet.make("[1,2]");
        assertFalse(irs.match(0));
        assertTrue(irs.match(1));
        assertTrue(irs.match(2));
        assertFalse(irs.match(3));
        assertEquals("[1:2]", irs.toString());
    }

    @Test
    public void test_in() {
        IntSet irs = IntSet.make("(1,2)");
        assertFalse(irs.match(0));
        assertFalse(irs.match(1));
        assertFalse(irs.match(2));
        assertFalse(irs.match(3));
        assertEquals("(1:2)", irs.toString());
    }

    @Test
    public void test_linfo() {
        IntSet irs = IntSet.make("[1,2)");
        assertFalse(irs.match(0));
        assertTrue(irs.match(1));
        assertFalse(irs.match(2));
        assertFalse(irs.match(3));
        assertEquals("[1:2)", irs.toString());
    }

    @Test
    public void test_rinon() {
        IntSet irs = IntSet.make("(1,2]");
        assertFalse(irs.match(0));
        assertFalse(irs.match(1));
        assertTrue(irs.match(2));
        assertFalse(irs.match(3));
        assertEquals("(1:2]", irs.toString());
    }

}
