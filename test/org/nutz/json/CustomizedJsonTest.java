package org.nutz.json;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nutz.lang.Strings;

public class CustomizedJsonTest {

    @Test
    public void test_trout_in_map() {
        Trout t = new Trout();
        t.setColor(Trout.COLOR.RED);
        t.setWeight(8.78f);
        Map<String, Trout> m = new HashMap<String, Trout>();
        m.put("t1", t);
        String exp = "{t1:\"Trout[RED](8.78)\"}";
        String json = Json.toJson(m, JsonFormat.compact().setQuoteName(false));
        assertEquals(exp, json);
    }

    public static class CJT01 {
        public String toJson(JsonFormat fmt) {
            return "01";
        }
    }

    @Test
    public void test_without_to_json_ann() {
        CJT01 obj = new CJT01();
        String s = Json.toJson(obj);
        assertEquals("01", s);
    }

    public static class CJT02 {
        public String toJson() {
            return "02";
        }
    }

    @Test
    public void test_without_to_json_ann_and_without_parameter() {
        CJT02 obj = new CJT02();
        String s = Json.toJson(obj);
        assertEquals("02", s);
    }

    public static class CJT03 {
        String abc;

        public String getAbc() {
            return abc.toUpperCase();
        }

    }

    @Test
    public void test_to_json_by_getter() {
        CJT03 obj = new CJT03();
        obj.abc = "xxx";
        assertEquals("{\"abc\":\"XXX\"}", Json.toJson(obj, JsonFormat.compact()));
    }

    public static class CJT04 {
        private String txt;
        @JsonField(value = "ag", ignore = true)
        private int age;

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

    }

    @Test
    public void test_json_ignore_read_write() {
        CJT04 obj = Json.fromJson(CJT04.class, "{txt:'abc',ag:10}");
        assertEquals("abc", obj.getTxt());
        assertEquals(10, obj.getAge());

        String json = Strings.trim(Json.toJson(obj, JsonFormat.compact().setQuoteName(false)));
        assertEquals("{txt:\"abc\"}", json);
    }
}
