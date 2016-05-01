package org.nutz.ioc.aop.config.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.impl.simple.AbcSimpleAop;
import org.nutz.ioc.aop.config.impl.simple.BeAop;
import org.nutz.ioc.aop.config.impl.simple.OneObject;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

public class SimpleAopConfigureTest extends Assert {

    @Test
    public void aop_maker_inject() {
        OneObject.COUNT = 0;
        Ioc ioc = new NutIoc(new AnnotationIocLoader(getClass().getPackage().getName()));
        ioc.get(BeAop.class);
        ioc.get(AbcSimpleAop.class);
        ioc.get(OneObject.class);
        ioc.depose();
        
        assertEquals(1, OneObject.COUNT);
    }
}
