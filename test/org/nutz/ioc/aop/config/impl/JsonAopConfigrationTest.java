package org.nutz.ioc.aop.config.impl;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;


public class JsonAopConfigrationTest {

    @Test
    public void test_jsonAop(){
        Ioc ioc = new NutIoc(new JsonLoader("org/nutz/ioc/aop/config/impl/jsonfile-aop.js"));
        Assert.assertTrue(ioc.getNames().length > 0);
        for (String name : ioc.getNames()) {
            ioc.get(null, name);
        }
        MyMI mi = ioc.get(MyMI.class, "myMI");
        assertTrue(mi.getTime() == 0);
        Pet2 pet2 = ioc.get(Pet2.class,"pet2");
        pet2.sing();
        assertTrue(mi.getTime() == 1);
        pet2.sing();
        assertTrue(mi.getTime() == 2);
    }
}
