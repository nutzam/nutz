package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class LinkedIntArrayTest {

    static LinkedIntArray LIA(int... es) {
        LinkedIntArray lia = new LinkedIntArray(2);
        for (int e : es)
            lia.push(e);
        return lia;
    }

    @Test
    public void test_re_push() {
        LinkedIntArray lia = new LinkedIntArray();
        lia.push(5);
        lia.popLast();
        lia.push(9);
        assertEquals(9, lia.last());
        assertEquals(1, lia.size());

    }

    @Test
    public void testPush() {
        LinkedIntArray lia = new LinkedIntArray();
        assertEquals(0, lia.size());
        lia.push(25).push(16);
        assertEquals(2, lia.size());
    }

    @Test
    public void testPopFirst() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertEquals(3, lia.size());
        assertEquals(23, lia.popFirst());
        assertEquals(2, lia.size());
    }

    @Test
    public void testPopLast() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertEquals(3, lia.size());
        assertEquals(67, lia.popLast());
        assertEquals(2, lia.size());
    }

    @Test
    public void testFirst() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertEquals(3, lia.size());
        assertEquals(23, lia.first());
        assertEquals(3, lia.size());
    }

    @Test
    public void testLast() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertEquals(3, lia.size());
        assertEquals(67, lia.last());
        assertEquals(3, lia.size());
    }

    @Test
    public void testSet() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertEquals(3, lia.size());
        lia.set(2, 80);
        assertEquals(80, lia.last());
        lia.set(0, 20);
        assertEquals(20, lia.first());
        lia.set(1, 60);
        assertEquals(60, lia.get(1));
        assertEquals(3, lia.size());
    }

    @Test
    public void testClear() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertEquals(3, lia.size());
        lia.clear();
        assertEquals(0, lia.size());
    }

    @Test
    public void testGet() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertEquals(23, lia.get(0));
        assertEquals(45, lia.get(1));
        assertEquals(67, lia.get(2));
    }

    @Test
    public void testIsEmpty() {
        LinkedIntArray lia = LIA(23, 45, 67);
        assertFalse(lia.isEmpty());
        lia.clear();
        assertTrue(lia.isEmpty());
    }

    @Test
    public void testToArray() {
        LinkedIntArray lia = LIA(23, 45, 67);
        int[] arr = lia.toArray();
        assertEquals(3, arr.length);
        assertEquals(23, arr[0]);
        assertEquals(45, arr[1]);
        assertEquals(67, arr[2]);
    }

}
