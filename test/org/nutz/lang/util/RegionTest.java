package org.nutz.lang.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Times;

public class RegionTest {

    @Test
    public void test_gt_lt() {
        assertFalse(Region.Int("(,2)").match(3));
        assertTrue(Region.Int("(1,2]").match(2));
        assertTrue(Region.Int("[2,)").match(3));
    }

    @Test
    public void test_equals() {
        assertFalse(Region.Int("[2)").match(1));
        assertTrue(Region.Int("[2]").match(2));
        assertFalse(Region.Int("(2]").match(3));
        assertTrue(Region.Int("(2)").match(3));
        assertFalse(Region.Int("(2)").match(2));
        assertTrue(Region.Int("(2)").match(1));
    }

    @Test
    public void test_int_regin() {
        assertFalse(Region.Int("(1,3)").match(1));
        assertTrue(Region.Int("(1,3)").match(2));
        assertFalse(Region.Int("(1,3)").match(3));

        assertFalse(Region.Int("[1,3]").match(-1));
        assertTrue(Region.Int("[1,3]").match(1));
        assertTrue(Region.Int("[1,3]").match(2));
        assertTrue(Region.Int("[1,3]").match(3));
        assertFalse(Region.Int("[1,3]").match(4));
    }

    @Test
    public void test_int_date() {
        assertFalse(Region.Date("(2013-9-20,2013-9-22)").match(Times.D("2013-9-20")));
        assertTrue(Region.Date("(2013-9-20,2013-9-22)").match(Times.D("2013-9-21")));
        assertFalse(Region.Date("(2013-9-20,2013-9-22)").match(Times.D("2013-9-22")));

        assertFalse(Region.Date("[2013-9-20,2013-9-22]").match(Times.D("2013-9-19")));
        assertTrue(Region.Date("[2013-9-20,2013-9-22]").match(Times.D("2013-9-20")));
        assertTrue(Region.Date("[2013-9-20,2013-9-22]").match(Times.D("2013-9-21")));
        assertTrue(Region.Date("[2013-9-20,2013-9-22]").match(Times.D("2013-9-22")));
        assertFalse(Region.Date("[2013-9-20,2013-9-22]").match(Times.D("2013-9-23")));
    }

    @Test
    public void test_auto_swap() {
        assertFalse(Region.Int("(3,1)").match(1));
        assertTrue(Region.Int("(3,1)").match(2));
        assertFalse(Region.Int("(3,1)").match(3));
    }
}
