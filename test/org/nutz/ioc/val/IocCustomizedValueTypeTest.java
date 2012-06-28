package org.nutz.ioc.val;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.dao.test.meta.Pet;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Lang;

public class IocCustomizedValueTypeTest {

    @Test
    public void test_simple_customized() {
        String json = "{xb:{name:{cc:'XiaoBai'}}}";
        Ioc2 ioc = new NutIoc(new MapLoader(json));
        ioc.addValueProxyMaker(new ValueProxyMaker() {
            public ValueProxy make(IocMaking ing, IocValue iv) {
                if ("cc".equalsIgnoreCase(iv.getType())) {
                    return new StaticValue("CC:" + iv.getValue());
                }
                return null;
            }

            public String[] supportedTypes() {
                return Lang.array("cc");
            }
        });

        Pet pet = ioc.get(Pet.class, "xb");
        assertEquals("CC:XiaoBai", pet.getName());
    }

}
