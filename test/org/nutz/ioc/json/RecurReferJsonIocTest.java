package org.nutz.ioc.json;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.map.MapLoader;

public class RecurReferJsonIocTest {

    public static class RA {

        public String nm;

        public RB rb;

    }

    public static class RB {

        public String nm;

        public RA ra;

    }

    @Test
    public void test_refer_each_other() {
        String s = "{";
        s += "a:{type:'org.nutz.ioc.json.RecurReferJsonIocTest$RA',";
        s += "fields:{nm:'A', rb:{refer:'b'}}";
        s += "},";
        s += "b:{type:'org.nutz.ioc.json.RecurReferJsonIocTest$RB',";
        s += "fields:{nm:'B', ra:{refer:'a'}}";
        s += "}";
        s += "}";

        Ioc ioc = new NutIoc(new MapLoader(s));
        RA a = ioc.get(RA.class, "a");
        assertEquals("A", a.nm);
        assertEquals("B", a.rb.nm);

        RB b = ioc.get(RB.class, "b");
        assertEquals("A", b.ra.nm);
        assertEquals("B", b.nm);
    }
}
