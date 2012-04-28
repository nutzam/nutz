package org.nutz.json.convert;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.json.JsonConvert;
import org.nutz.lang.Streams;

import static org.junit.Assert.*;

public class JsonConvertTest { 
    JsonConvert jc = new JsonConvert();
    Object obj;
    
    @Before
    public void before(){
        obj = Json.fromJson(Streams.fileInr("org/nutz/json/convert/map.txt"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void simple(){
        Map<String, Object> dest = (Map<String, Object>) jc.convert(obj, "org/nutz/json/convert/1");
        assertEquals(dest.get("b1"), "value1");
    }
    @SuppressWarnings("unchecked")
    @Test
    public void test(){
        Map<String, Object> dest = (Map<String, Object>) jc.convert(obj, "org/nutz/json/convert/2");
        System.out.println(Json.toJson(dest));
        assertEquals(dest.get("b1"), "value1");
    }
    @SuppressWarnings("unchecked")
    @Test
    public void arrayTest(){
        Map<String, Object> dest = (Map<String, Object>) jc.convert(obj, "org/nutz/json/convert/3");
        System.out.println(Json.toJson(dest));
        assertTrue(((Map<String, Object>)dest.get("jk")).get("tt") instanceof List);
    }
    @Test
    public void array2Test(){
        obj = Json.fromJson(Streams.fileInr("org/nutz/json/convert/list.txt"));
        Object dest = jc.convert(obj, "org/nutz/json/convert/list");
        System.out.println(Json.toJson(dest));
        assertTrue(dest instanceof List);
    }
}
