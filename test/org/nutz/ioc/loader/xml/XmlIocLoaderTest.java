package org.nutz.ioc.loader.xml;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.xml.meta.Bee;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;

public class XmlIocLoaderTest {

    IocLoader getNew(String fileName) {
        return new XmlIocLoader(fileName);
    }

    @Test
    public void testXmlIocLoader() throws ObjectLoadException {
        IocLoader iocLoader = getNew("org/nutz/ioc/loader/xml/conf/zzh-offered.xml");
        assertTrue(iocLoader.getName() != null);
        assertTrue(iocLoader.getName().length > 0);

        for (String name : iocLoader.getName()) {
            assertNotNull(name);
            assertNotNull(iocLoader.load(null, name));
            IocObject iocObject = iocLoader.load(null, name);
            if (iocObject.hasArgs()) {
                for (IocValue iocValue : iocObject.getArgs()) {
                    iocValue.getType();
                    iocValue.getValue();
                    checkValue(iocValue);
                }
            }
            if (iocObject.getFields() != null) {
                for (IocField iocField : iocObject.getFields()) {
                    assertNotNull(iocField.getName());
                    if (iocField.getValue() != null) {
                        IocValue iocValue = iocField.getValue();
                        checkValue(iocValue);
                    }
                }
            }
        }
        iocLoader.load(null, "obj").getFields()[0].getValue().getValue();
    }

    private void checkValue(IocValue iocValue) {
        iocValue.getType();
        if (iocValue.getValue() != null && iocValue.getValue() instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) iocValue.getValue();
            for (Object object : collection) {
                assertNotNull(object);
            }
        }
    }

    @Test
    public void test_simple_case() {
        Ioc ioc = new NutIoc(getNew("org/nutz/ioc/loader/xml/conf/simple.xml"));
        Bee c = ioc.get(Bee.class, "C");
        assertEquals("TheC", c.getName());
        assertEquals(15, c.getAge());
        assertEquals("TheQueen", c.getMother().getName());
        assertEquals(3, c.getFriends().size());
        assertEquals("TheA", c.getFriends().get(0).getName());
        assertEquals("TheB", c.getFriends().get(1).getName());
        assertEquals(1,c.getMap().size());
        assertEquals("ABC",c.getMap().get("abc"));
    }
    
}
