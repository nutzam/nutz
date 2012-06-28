package org.nutz.json;

import static org.junit.Assert.*;

import java.io.Reader;
import java.util.Map;

import org.junit.Test;

import org.nutz.lang.Streams;

@SuppressWarnings("unchecked")
public class JsonCharsetTest {

    @Test
    public void test_simple_map() {
        String str = "{name:\"张志昊\"}";
        Map<String, String> map = (Map<String, String>) Json.fromJson(str);
        String json = Json.toJson(map, JsonFormat.compact().setQuoteName(false));
        assertEquals(str, json);
    }

    @Test
    public void test_zh_CN_from_file() throws Exception {
        Reader reader = Streams.fileInr("org/nutz/json/zh_CN.txt");
        Map<String, String> map = (Map<String, String>) Json.fromJson(reader);
        assertEquals("张", map.get("name"));
    }

}
