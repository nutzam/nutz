package org.nutz.json.recursionQuoted;

import java.util.Map;

import org.junit.Test;
import org.nutz.el.El;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;

import static org.junit.Assert.*;

/**
 * 循环引用测试
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class RecursionQuotedTest {
    @Test
    public void testToJson(){
        A a = new A();
        B b = new B();
        a.name = "testa";
        b.name = "testb";
        a.b = b;
        b.a = a;
        String tt = "{\"name\":\"testa\",\"b\":{\"name\":\"testb\",\"a\":\"$nutz.json::root\"}}";
        assertEquals(tt, Json.toJson(a,JsonFormat.compact().setNutzJson(true)));
    }
    
    @Test
    public void testToObj(){
        A a = new A();
        B b = new B();
        a.name = "testa";
        b.name = "testb";
        a.b = b;
        b.a = a;
        
        A ta = (A)Json.fromJson(A.class, Json.toJson(a, JsonFormat.compact().setNutzJson(true)));
        assertEquals("testa", ta.b.a.name);
    }
    
    @Test
    public void testJsonEl(){
        assertEquals(2, ((El)((Json.fromJson(Map.class, "{'a':\"$nutz.el::1+1\"}")).get("a"))).eval(Lang.context()));
    }
}
