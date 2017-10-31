package org.nutz.lang.util;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class LinkedArrayTest {

    private LinkedArray<String> la;

    @Before
    public void setUp() throws Exception {
        la = new LinkedArray<String>(2);
        la.pushAll("A", "B", "C", "D", "E", "F", "G");
    }

    @Test
    public void test_iterator() {
        la = new LinkedArray<String>(2);
        la.pushAll("A", "B", "C", "D", "E", "F", "G");
        la.popFirst();
        la.popFirst();
        la.popFirst();
        Iterator<String> it = la.iterator();
        assertEquals("D", it.next());
        assertEquals("E", it.next());
        assertEquals("F", it.next());
        assertEquals("G", it.next());
        assertFalse(it.hasNext());
        assertNull(it.next());
        assertNull(it.next());
    }

    @Test
    public void test_re_push() {
        la = new LinkedArray<String>();
        la.push("A").popLast();
        la.push("F");
        assertEquals("F", la.last());
        assertEquals(1, la.size());
    }

    @Test
    public void testNormal() {
        assertEquals(7, la.size());
        assertEquals("A", la.first());
        assertEquals("G", la.last());
    }

    @Test
    public void testPopfirst() {
        assertEquals("A", la.popFirst());
        assertEquals("B", la.popFirst());
        assertEquals("C", la.popFirst());
        assertEquals("D", la.popFirst());
        assertEquals("E", la.popFirst());
        assertEquals("F", la.popFirst());
        assertEquals("G", la.popFirst());
        assertEquals(0, la.size());
    }

    @Test
    public void testPoplast() {
        assertEquals("G", la.popLast());
        assertEquals("F", la.popLast());
        assertEquals("E", la.popLast());
        assertEquals("D", la.popLast());
        assertEquals("C", la.popLast());
        assertEquals("B", la.popLast());
        assertEquals("A", la.popLast());
        assertEquals(0, la.size());
    }

    @Test
    public void testPop() {
        la.popFirst();
        assertEquals("B", la.first());
        la.popFirst();
        assertEquals("C", la.first());
        la.popLast();
        assertEquals("F", la.last());
        la.popLast();
        assertEquals("E", la.last());
    }

    @Test
    public void testToString() {
        assertEquals("[\"A\", \"B\", \"C\", \"D\", \"E\", \"F\", \"G\"]", la.toString());
    }

    @Test
    public void testGetSet() {
        assertEquals("C", la.get(2));
        la.set(2, "$");
        assertEquals("$", la.get(2));
    }

    @Test
    public void testGetSetOutOfBound() {
        try {
            la.get(-1);
            fail();
        }
        catch (Exception e) {}
        try {
            la.get(la.size());
            fail();
        }
        catch (Exception e) {}
        try {
            la.set(-1, "#");
            fail();
        }
        catch (Exception e) {}
        try {
            la.set(la.size(), "#");
            fail();
        }
        catch (Exception e) {}

    }

    @Test
    public void testClear() {
        assertFalse(la.isEmpty());
        la.clear();
        assertTrue(la.isEmpty());
        assertEquals(0, la.size());
    }

}
