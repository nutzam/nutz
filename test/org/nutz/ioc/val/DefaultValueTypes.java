package org.nutz.ioc.val;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.impl.ComboContext;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;

public class DefaultValueTypes {
    
    public String name;

    @InjectName("obj")
    public static class TestReferContext {
        IocContext ic;
    }

    @Test
    public void test_refer_context() {
        IocContext context = new ScopeContext("abc");
        String json = "{obj:{singleton:false,fields:{ic:{refer:'$conText'}}}}";
        Ioc2 ioc = new NutIoc(new MapLoader(json), context, "abc");
        TestReferContext trc = ioc.get(TestReferContext.class);
        assertTrue(context == trc.ic);

        IocContext context2 = new ScopeContext("rrr");
        trc = ioc.get(TestReferContext.class, "obj", context2);
        assertTrue(trc.ic instanceof ComboContext);
    }
    
    @Test
    public void test_el() {
        IocContext context = new ScopeContext("abc");
        String json = "{obj:{type:'org.nutz.ioc.val.DefaultValueTypes', fields:{name:{el:'sys[\"os.arch\"]'}}}}";
        System.out.println(Json.toJson(Json.fromJson(json)));
        Ioc2 ioc = new NutIoc(new MapLoader(json), context, "abc");
        DefaultValueTypes self = ioc.get(DefaultValueTypes.class, "obj");
        assertEquals(System.getProperties().get("os.arch"), self.name);
    }
}
