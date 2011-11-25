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
    public void test1(){
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
}
