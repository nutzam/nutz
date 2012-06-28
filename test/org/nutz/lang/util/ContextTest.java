package org.nutz.lang.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import org.junit.Test;

public class ContextTest {
    @Test
    public void test_get_primitive_type() {
        Context context = new SimpleContext();
        context.set("a", "123");
        context.set("b", "10.2");
        context.set("c", "true");
        context.set("d", "string");

        assertEquals(123, context.getInt("a"));
        assertTrue(10.2f == context.getFloat("b"));
        assertTrue(context.getBoolean("c"));
        assertEquals("string", context.getString("d"));
    }

    @Test
    public void test_normal_object() {
        Date now = new Date();
        Context context = new SimpleContext();
        context.set("a", "123");
        context.set("b", now);
        assertEquals(now, context.get("b"));

        context.clear();
        assertNull(context.get("b"));
    }
}
