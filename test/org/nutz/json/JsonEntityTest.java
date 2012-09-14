package org.nutz.json;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.meta.JENObj;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class JsonEntityTest {

    @Test
    public void test_entity_parse() {
        JsonEntity jen = Json.getEntity(Mirror.me(JENObj.class));
        assertEquals(3, jen.getFields().size());

        assertEquals(long.class, jen.getField("id").getGenericType());
        assertEquals(String.class, jen.getField("name").getGenericType());
        assertEquals(int.class, jen.getField("age").getGenericType());

    }

    @Test
    public void test_simple_JENObj() {
        String str = "{id:9999999999, name:'abc', age:10}";
        JENObj obj = Json.fromJson(JENObj.class, str);

        assertEquals(9999999999L, obj.getObjId());
        assertEquals("abc", obj.getName());
        assertEquals(10, obj.getAge());

        str = Json.toJson(obj);

        obj = Json.fromJson(JENObj.class, str);

        assertEquals(9999999999L, obj.getObjId());
        assertEquals("abc", obj.getName());
        assertEquals(10, obj.getAge());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_JENObj_output() {
        JENObj obj = new JENObj();
        String str = Json.toJson(obj, JsonFormat.nice().setIgnoreNull(false));
        Map<String, Object> map = (Map<String, Object>) Json.fromJson(str);
        String[] keys = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(keys);
        String keyStr = Lang.concat(",", keys).toString();
        assertEquals("age,id,name", keyStr);

    }

}
