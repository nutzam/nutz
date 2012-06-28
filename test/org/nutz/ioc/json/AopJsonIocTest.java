package org.nutz.ioc.json;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.pojo.Mammal;
import org.nutz.ioc.loader.json.JsonLoader;

public class AopJsonIocTest {

    @Test
    public void test_simple() {
        IocLoader il = new JsonLoader("org/nutz/ioc/json/aop.js");
        Ioc ioc = new NutIoc(il);
        StringBuilder sb = ioc.get(StringBuilder.class, "sb");
        Mammal fox = ioc.get(Mammal.class, "fox");

        assertEquals("Fox", fox.getName());
        assertEquals("B:getName0;A:getName0;", sb.toString());
        sb.delete(0, sb.length());
        fox.getName();
        fox.getName();
        assertEquals("B:getName0;A:getName0;B:getName0;A:getName0;", sb.toString());

    }
}
