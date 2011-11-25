package org.nutz.json.recursionQuoted;

import org.junit.Test;
import org.nutz.json.Json;

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
        String tt = "{\n"+
                "   \"name\" :\"testa\",\n"+
                "   \"b\" :{\n"+
                "      \"name\" :\"testb\",\n"+
                "      \"a\" :${root}\n"+
                "   }\n"+
                "}";
        assertEquals(tt, Json.toJson(a));
    }
    
    @Test
    public void testToObj(){
        A a = new A();
        B b = new B();
        a.name = "testa";
        b.name = "testb";
        a.b = b;
        b.a = a;
        
        A ta = (A)Json.fromJson(Json.toJson(a));
        assertEquals("testa", ta.b.a.name);
    }
}
