package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LinkedCharArrayTest {

    private LinkedCharArray lia;

    @Before
    public void setUp() throws Exception {
        lia = new LinkedCharArray(2);
        lia.push("ABCDEFG");
    }

    @Test
    public void test_re_push() {
        lia = new LinkedCharArray();
        lia.push('A').popLast();
        lia.push('F');
        assertEquals('F', lia.last());
        assertEquals(1, lia.size());
    }

    @Test
    public void testNormal() {
        assertEquals(7, lia.size());
        assertEquals('A', lia.first());
        assertEquals('G', lia.last());
    }

    @Test
    public void testPopfirst() {
        assertEquals('A', lia.popFirst());
        assertEquals('B', lia.popFirst());
        assertEquals('C', lia.popFirst());
        assertEquals('D', lia.popFirst());
        assertEquals('E', lia.popFirst());
        assertEquals('F', lia.popFirst());
        assertEquals('G', lia.popFirst());
        assertEquals(0, lia.size());
    }

    @Test
    public void testPoplast() {
        assertEquals('G', lia.popLast());
        assertEquals('F', lia.popLast());
        assertEquals('E', lia.popLast());
        assertEquals('D', lia.popLast());
        assertEquals('C', lia.popLast());
        assertEquals('B', lia.popLast());
        assertEquals('A', lia.popLast());
        assertEquals(0, lia.size());
    }

    @Test
    public void testPop() {
        lia.popFirst();
        assertEquals('B', lia.first());
        lia.popFirst();
        assertEquals('C', lia.first());
        lia.popLast();
        assertEquals('F', lia.last());
        lia.popLast();
        assertEquals('E', lia.last());
    }

    @Test
    public void testToString() {
        assertEquals("ABCDEFG", lia.toString());
    }

    @Test
    public void testGetSet() {
        assertEquals('C', lia.get(2));
        lia.set(2, '$');
        assertEquals('$', lia.get(2));
    }

    @Test
    public void testGetSetOutOfBound() {
        try {
            lia.get(-1);
            fail();
        }
        catch (Exception e) {}
        try {
            lia.get(lia.size());
            fail();
        }
        catch (Exception e) {}
        try {
            lia.set(-1, '#');
            fail();
        }
        catch (Exception e) {}
        try {
            lia.set(lia.size(), '#');
            fail();
        }
        catch (Exception e) {}

    }

    @Test
    public void testClear() {
        assertFalse(lia.isEmpty());
        lia.clear();
        assertTrue(lia.isEmpty());
        assertEquals(0, lia.size());
    }

    @Test
    public void testEndWith() {
        assertTrue(lia.endsWith("FG"));
        assertFalse(lia.endsWith((String) null));
        assertFalse(lia.endsWith("M"));

        assertTrue(lia.endsWith(new char[]{'F', 'G'}));
        assertFalse(lia.endsWith((char[]) null));
        assertFalse(lia.endsWith(new char[]{'M'}));
    }
}
