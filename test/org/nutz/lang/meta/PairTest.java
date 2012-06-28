package org.nutz.lang.meta;

import static org.junit.Assert.*;

import org.junit.Test;

public class PairTest {

    @Test
    public void test_equal(){
        Pair<String> p = Pair.create("abc=\"bbb\"");
        Pair<String> p2 = Pair.create("abc=\"bbb\"");
        assertTrue(p.equals(p2));
    }
    
    @Test
    public void test_create() {
        Pair<String> p = Pair.create("abc=\"bbb\"");
        assertEquals("abc", p.getName());
        assertEquals("bbb", p.getValue());
    }
}
