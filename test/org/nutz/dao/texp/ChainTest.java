package org.nutz.dao.texp;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.Lang;

public class ChainTest extends DaoCase {

    /**
     * Issue 93
     */
    @Test
    public void test_chain_from_object() {
        Worker w = new Worker();
        w.age = 20;
        w.name = "zzh";
        Chain c = Chain.from(w, FieldMatcher.make("age|name", null, true));
        Map<String, Object> map = c.toMap();
        assertEquals(2, map.size());
        assertEquals("zzh", map.get("name"));
        assertEquals(20, ((Short) map.get("age")).intValue());
    }

    /**
     * Issue 93
     */
    @Test
    public void test_chain_from_map() {
        Map<?, ?> map = Lang.map("{a:12,b:true,c:'haha'}");
        Chain c = Chain.from(map);
        Map<String, Object> map2 = c.toMap();
        assertTrue(Lang.equals(map, map2));
    }

    /**
     * Issue 93
     */
    @Test
    public void test_chain_to_object() {
        Chain c = Chain.from(Lang.map("{name:'zzh',age:30}"));
        Worker w = c.toObject(Worker.class);
        assertEquals("zzh", w.name);
        assertEquals(30, w.age);
    }

}
