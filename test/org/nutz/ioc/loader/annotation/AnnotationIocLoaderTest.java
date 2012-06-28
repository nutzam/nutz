package org.nutz.ioc.loader.annotation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.meta.IocObject;

public class AnnotationIocLoaderTest {

    IocLoader iocLoader = new AnnotationIocLoader("org.nutz.ioc.loader.annotation.meta");

    @Test
    public void testGetName() {
        assertNotNull(iocLoader.getName());
        assertTrue(iocLoader.getName().length > 0);
    }

    @Test
    public void testHas() {
        assertTrue(iocLoader.has("classA"));
    }

    @Test
    public void testLoad() throws Throwable {
        IocObject iocObject = iocLoader.load(null, "classB");
        assertNotNull(iocObject);
        assertNotNull(iocObject.getFields());
        assertTrue(iocObject.getFields().length == 1);
        assertEquals("refer", iocObject.getFields()[0].getValue().getType());
    }

}
