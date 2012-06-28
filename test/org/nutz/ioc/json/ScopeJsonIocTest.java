package org.nutz.ioc.json;

import java.util.Map;

import org.junit.Test;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.json.pojo.Animal;

import static org.junit.Assert.*;
import static org.nutz.ioc.json.Utils.*;

public class ScopeJsonIocTest {

    @Test
    public void test_simple_scope() {
        Ioc2 ioc = I(    J("f1", "scope:'app',fields:{name:'F1'}"),
                        J("f2", "scope:'MyScope',fields:{name:'F2'}"));

        Animal f1 = ioc.get(Animal.class, "f1");
        assertEquals("F1", f1.getName());

        Animal f2 = ioc.get(Animal.class, "f2");
        assertEquals("F2", f2.getName());
        Animal f22 = ioc.get(Animal.class, "f2");
        assertEquals("F2", f22.getName());
        assertFalse(f2 == f22);

        ScopeContext ic = new ScopeContext("MyScope");
        Map<String, ObjectProxy> map = ic.getObjs();
        f2 = ioc.get(Animal.class, "f2", ic);
        assertEquals("F2", f2.getName());
        f22 = ioc.get(Animal.class, "f2", ic);
        assertEquals("F2", f22.getName());
        assertTrue(f2 == f22);
        assertEquals(1, map.size());

        ioc.get(Animal.class, "f1", ic);

        assertEquals(1, map.size());

    }
    
    @Test
    public void test_refer_from_diffenent_scope(){
        Ioc2 ioc = I(    J("f1", "type : 'org.nutz.ioc.json.pojo.Animal' , scope:'app',fields:{name:'F1'}"),
                        J("f2", "type : 'org.nutz.ioc.json.pojo.Animal' , scope:'MyScope',fields:{name:{refer : 'f3'}}"),
                        J("f3", "type : 'org.nutz.ioc.json.pojo.Animal' , scope:'MyScope'}"));
        ioc.get(null, "f2");
    }

}
