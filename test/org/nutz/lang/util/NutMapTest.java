package org.nutz.lang.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class NutMapTest {
    @Test
    public void test_add_element_with_same_key() {
        NutMap nutMap = new NutMap();
        nutMap.add("a", "a1").add("a", "a2");

        List<?> key = (List<?>) nutMap.get("a");
        assertEquals("a1", key.get(0));
        assertEquals("a2", key.get(1));
    }

    @Test
    public void test_get_element_cast_type() {
        NutMap nutMap = new NutMap();
        nutMap.add("a", "123").add("b", 123);
        assertEquals(123, nutMap.getInt("a"));
        assertEquals("123", nutMap.getString("b"));
    }
}
