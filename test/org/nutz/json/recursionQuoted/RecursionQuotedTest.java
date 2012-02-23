package org.nutz.json.recursionQuoted;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

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
}
