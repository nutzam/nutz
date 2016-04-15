package org.nutz.ioc.loader.properties;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.json.Json;

public class PropertiesIocLoaderTest {

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testLoadIocLoadingString() throws ObjectLoadException {
        PropertiesIocLoader pp = new PropertiesIocLoader();
        pp.put("ioc.ds.type", "org.nutz.dao.impl.SimpleDataSource");
        //pp.put("ioc.ds.args.0", "abc");
        pp.put("ioc.ds.fields.username", "root");
        pp.put("ioc.ds.fields.password", "root");
        pp.put("ioc.ds.fields.jdbcUrl", "jdbc:mysql://127.0.0.1/nutzbook");
        System.out.println(Json.toJson(pp));
        System.out.println(Json.toJson(pp.getName()));
        System.out.println(Json.toJson(pp.load(null, "ds")));
        assertEquals("root", pp.load(null, "ds").getFields().get("username").getValue().getValue());
        
//        NutIoc ioc = new NutIoc(pp);
//        PropertiesProxy p = ioc.get(PropertiesProxy.class, "conf");
//        assertEquals(p, pp);
//    	ioc.depose();
    }
}
