package org.nutz.lang.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;

public class NutMapTest {

    @Test
    public void test_match_0() {
        assertTrue(Lang.map("a:'23',x:100").match(Lang.map("a:'23',x:100,y:48")));
        assertTrue(Lang.map("a:'23',x:100").match(Lang.map("a:['a','23','b'],x:100,y:48")));
        assertTrue(Lang.map("{}").match(Lang.map("a:['a','23','b'],x:100,y:48")));

        assertFalse(Lang.map("a:'20',x:100").match(Lang.map("a:'23',x:100,y:48")));
        assertFalse(Lang.map("a:'23',x:101").match(Lang.map("a:['a','23','b'],x:100,y:48")));
    }

    @Test
    public void test_add_element_with_same_key() {
        NutMap nutMap = new NutMap();
        nutMap.addv("a", "a1").addv("a", "a2");

        List<?> key = (List<?>) nutMap.get("a");
        assertEquals("a1", key.get(0));
        assertEquals("a2", key.get(1));
    }

    @Test
    public void test_get_element_cast_type() {
        NutMap nutMap = new NutMap();
        nutMap.addv("a", "123").addv("b", 123);
        assertEquals(123, nutMap.getInt("a"));
        assertEquals("123", nutMap.getString("b"));
    }

}
