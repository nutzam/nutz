package org.nutz.lang.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FloatSetTest {

    @Test
    public void test_inon() {
        FloatSet irs = FloatSet.make("[.1,.2]");
        assertFalse(irs.match(0.09f));
        assertTrue(irs.match(0.1f));
        assertTrue(irs.match(0.2f));
        assertFalse(irs.match(0.2001f));
        assertEquals("[0.1:0.2]", irs.toString());
    }

    @Test
    public void test_in() {
        FloatSet irs = FloatSet.make("(.1:.2)");
        assertFalse(irs.match(0f));
        assertFalse(irs.match(0.1f));
        assertFalse(irs.match(0.100000001f));
        assertFalse(irs.match(0.2f));
        assertFalse(irs.match(0.3f));
        assertEquals("(0.1:0.2)", irs.toString());
    }

    @Test
    public void test_linfo() {
        FloatSet irs = FloatSet.make("[.1:.2)");
        assertFalse(irs.match(0f));
        assertTrue(irs.match(0.1f));
        assertFalse(irs.match(0.2f));
        assertFalse(irs.match(0.3f));
        assertEquals("[0.1:0.2)", irs.toString());
    }

    @Test
    public void test_rinon() {
        FloatSet irs = FloatSet.make("(.1,.2]");
        assertFalse(irs.match(0f));
        assertFalse(irs.match(0.1f));
        assertTrue(irs.match(0.2f));
        assertFalse(irs.match(0.3f));
        assertEquals("(0.1:0.2]", irs.toString());
    }

}
