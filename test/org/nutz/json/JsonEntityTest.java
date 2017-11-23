package org.nutz.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.json.impl.JsonEntityFieldMakerImpl;
import org.nutz.json.meta.JENObj;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.inject.InjectBySetter;

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

    @Test
    public void test_entity_field_maker() {
        Json.clearEntityCache();
        JENObj obj = new JENObj();
        obj.setAge(100);
        obj.setName("name");
        obj.setObjId(9l);
        Json.setDefaultFieldMaker(new AbstractJsonEntityFieldMaker() {
            @Override
            public JsonEntityField make(Mirror<?> mirror, Method method) {
                return null;
            }
            @Override
            public JsonEntityField make(Mirror<?> mirror, Field field) {
                return null;
            }
        });
        assertEquals("{}", Json.toJson(obj, JsonFormat.compact()));
        Json.clearEntityCache();
        Json.setDefaultFieldMaker(new AbstractJsonEntityFieldMaker() {
            @Override
            public JsonEntityField make(Mirror<?> mirror, Method method) {
                if (method.getName().equals("setName")) {
                    String fn = Strings.lowerFirst(method.getName().substring(3));
                    return JsonEntityField.eval(mirror, "another_name", method.getParameterTypes()[0], mirror.getEjecting(fn), new InjectBySetter(method));
                }
                return null;
            }
            @Override
            public JsonEntityField make(Mirror<?> mirror, Field field) {
                return JsonEntityField.eval(mirror, "test_" + field.getName(), field.getType(), mirror.getEjecting(field), mirror.getInjecting(field.getName()));
            }
        });
        String json = Json.toJson(obj, JsonFormat.compact());
        assertTrue(json.contains("\"test_objId\":9"));
        assertTrue(json.contains("\"test_name\":\"name\""));
        assertTrue(json.contains("\"test_age\":100"));
        assertTrue(json.contains("\"another_name\":\"name\""));
        Json.clearEntityCache();
        Json.setDefaultFieldMaker(new JsonEntityFieldMakerImpl());
    }
}
