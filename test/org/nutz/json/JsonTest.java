package org.nutz.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.nutz.dao.test.meta.Base;
import org.nutz.ioc.meta.IocValue;
import org.nutz.json.meta.JA;
import org.nutz.json.meta.JB;
import org.nutz.json.meta.JC;
import org.nutz.json.meta.JENObj;
import org.nutz.json.meta.JMapItem;
import org.nutz.json.meta.OuterClass;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.lang.stream.StringOutputStream;
import org.nutz.lang.util.NutType;

@SuppressWarnings({"unchecked"})
public class JsonTest {

    @Test
    public void test_empty_obj_toJson() {
        String j = Json.toJson(new Person(), JsonFormat.compact().setQuoteName(true));
        assertEquals("{\"age\":0,\"num\":0}", j);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_empty_array_field() {
        String str = "{a:[],b:100}";
        Map<String, Object> map = (Map<String, Object>) Json.fromJson(str);
        assertEquals(100, ((Integer) map.get("b")).intValue());
        assertEquals(0, ((List) map.get("a")).size());
    }

    @Test
    public void test_map_in_map() {
        String str = "{a:{},b:100}";
        Map<String, Object> map = (Map<String, Object>) Json.fromJson(str);
        assertEquals(100, ((Integer) map.get("b")).intValue());
    }

    @Test
    public void test_bear_error_end_list() {
        int[] is = Json.fromJson(int[].class, "[2,]");
        assertEquals(2, is[0]);
    }

    @Test
    public void test_bear_error_end_map() {
        Person p = Json.fromJson(Person.class, "{name:'a',}");
        assertEquals("a", p.getName());
    }

    @Test
    public void test_toJson_with_enum() {
        Person[] ps = new Person[2];

        ps[0] = new Person();
        ps[0].setName("A");
        ps[0].setSex(PersonSex.MAN);

        ps[1] = new Person();
        ps[1].setName("B");
        ps[1].setSex(PersonSex.MAN);

        String str = Json.toJson(ps);

        Person[] ps2 = Json.fromJson(Person[].class, str);
        assertEquals(2, ps2.length);
        assertEquals(PersonSex.MAN, ps2[0].getSex());
        assertEquals(PersonSex.MAN, ps2[1].getSex());
        assertEquals("A", ps2[0].getName());
        assertEquals("B", ps2[1].getName());
    }

    // TODO zozoh : 如果没人有意见，这个 case 被我第二次注意到时，将被删除
    @Test
    public void test_toJson_with_super_field() {
        // Xyz x = new Xyz();
        // x.id = 100;
        // x.name = "haha";
        // x.setXyz("!!!");
        // String str = Json.toJson(x);
        // Xyz x2 = Json.fromJson(Xyz.class, str);
        // assertEquals(x.getXyz(), x2.getXyz());
        // assertEquals(x.id, x2.id);
        // assertEquals(x.name, x2.name);
    }

    @Test
    public void test_map_class_item() {
        String path = "org.nutz.json.meta";
        String s = String.format("{map:{a:'%s.JA', b:'%s.JB'}}", path, path);
        JMapItem jmi = Json.fromJson(JMapItem.class, s);
        assertEquals(2, jmi.getMap().size());
        assertEquals(JB.class, jmi.getMap().get("b"));
    }

    @Test
    public void test_map_class_item_as_string() {
        String path = "org.nutz.json.meta";
        String s = String.format("{list:['%s.JA','%s.JB']}", path, path);
        JMapItem jmi = Json.fromJson(JMapItem.class, s);
        assertEquals(2, jmi.getList().size());
        assertEquals(JA.class, jmi.getList().get(0));
        assertEquals(JB.class, jmi.getList().get(1));
    }

    @Test
    public void test_unknown_field_in_json_string() {
        Abc abc = Json.fromJson(Abc.class, "{id:2,name:'zzh',uuab:'ttt'}");
        assertEquals(2, abc.id);
        assertEquals("zzh", abc.name);
    }

    @Test
    public void field_name_with_colon() {
        Map<?, ?> map = (Map<?, ?>) Json.fromJson("{'i\"d:':6};");
        assertEquals(6, map.get("i\"d:"));
    }

    @Test
    public void with_var_ioc_as_prefix() {
        Map<?, ?> map = (Map<?, ?>) Json.fromJson("var ioc = {id:6};");
        assertEquals(6, map.get("id"));

        map = (Map<?, ?>) Json.fromJson("\t\n\r   var ioc= {id:6};");
        assertEquals(6, map.get("id"));
    }

    @Test
    public void born_with_map() {
        Map<?, ?> map = Json.fromJson(Map.class, "{a:'A'}");
        assertEquals("A", map.get("a"));
    }

    @Test
    public void when_name_has_unsupport_char() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("/tt", 123);
        assertEquals("{\"/tt\":123}", Json.toJson(map, JsonFormat.compact().setQuoteName(false)));
    }

    @Test
    public void when_name_has_number_char_at_first() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("3T", 123);
        assertEquals("{\"3T\":123}", Json.toJson(map, JsonFormat.compact().setQuoteName(false)));
    }

    @Test
    public void testSimpleObject() {
        assertEquals("6.5", Json.toJson(6.5));
        assertEquals("\"json\"", Json.toJson("json"));
        int[] ints = new int[0];
        assertEquals("[]", Json.toJson(ints));
        ints = new int[1];
        ints[0] = 65;
        assertEquals("[65]", Json.toJson(ints));
        assertEquals(65, Json.fromJson(Lang.inr("65")));
        assertEquals(Float.valueOf("65"), Json.fromJson(float.class, Lang.inr("65")));
        assertEquals(ints[0], Json.fromJson(int[].class, Lang.inr("[65]"))[0]);
    }

    @Test
    public void testBoolean() {
        assertTrue(Json.fromJson(boolean.class, Lang.inr("true")));
        try {
            Json.fromJson(boolean.class, Lang.inr("ture"));
            fail();
        }
        catch (JsonException e) {}
        assertFalse(Json.fromJson(boolean.class, Lang.inr("false")));
        assertTrue(((Boolean) Json.fromJson(Lang.inr("true"))).booleanValue());
        assertFalse(((Boolean) Json.fromJson(Lang.inr("false"))).booleanValue());
    }

    @Test
    public void testFloat() {
        assertEquals(Float.valueOf(2.3f), Json.fromJson(float.class, Lang.inr("2.3")));
        assertEquals((Float) 2.3f, Json.fromJson(Float.class, Lang.inr("2.3")));
        assertEquals(Float.valueOf(.3f), Json.fromJson(float.class, Lang.inr(".3")));
    }

    @Test
    public void testLongg() {
        assertEquals(87L, Json.fromJson(long.class, Lang.inr("87")).longValue());
        assertEquals(87L, ((Long) Json.fromJson(Lang.inr("87L"))).longValue());
    }

    @Test
    public void testDatetime() {
        java.util.Date date = Json.fromJson(java.util.Date.class,
                                            Lang.inr("\"2008-05-16 14:35:43\""));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(2008, cal.get(Calendar.YEAR));
        assertEquals(4, cal.get(Calendar.MONTH));
        assertEquals(16, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(35, cal.get(Calendar.MINUTE));
        assertEquals(43, cal.get(Calendar.SECOND));
    }

    @Test
    public void testSimpleAbc() {
        String s = "{\"id\":45,\"name\":'xyz'}";
        Abc abc = Json.fromJson(Abc.class, Lang.inr(s));
        assertEquals(45, abc.id);
        assertEquals("xyz", abc.name);
    }

    @Test
    public void testAllTypesInMap() throws FileNotFoundException {
        Map<String, Object> map = (Map<String, Object>) Json.fromJson(new InputStreamReader(getClass().getResourceAsStream("/org/nutz/json/types.txt")));
        assertTrue((Boolean) map.get("true"));
        assertFalse((Boolean) map.get("false"));
        assertNull(map.get("null"));
        assertTrue(34 == (Integer) map.get("int"));
        assertTrue(67L == (Long) map.get("long"));
        assertTrue(7.69 == (Double) map.get("double"));
        assertTrue(8.79f == (Float) map.get("float"));
        List<?> ary = (List<?>) map.get("array");
        assertEquals(2, ary.size());
        assertEquals("abc", ary.get(0));
        List<?> coll = ary;
        assertTrue(45 == (Integer) coll.get(1));
    }

    @Test
    public void testSimpleString() {
        String s = (String) Json.fromJson(Lang.inr(""));
        assertEquals(null, s);

        s = (String) Json.fromJson(Lang.inr("\"\""));
        assertEquals("", s);
    }

    @Test
    public void testSimpleMap() {
        String s = "{id:45,m:{x:1},name:'xyz'}";
        Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr(s));
        assertEquals(45, map.get("id"));
        assertEquals("xyz", map.get("name"));
        Map<?, ?> m = (Map<?, ?>) map.get("m");
        assertEquals(1, m.get("x"));

    }

    @Test
    public void testSimpleMap_asMap() {
        String s = "{id:45,m:1,name:'xyz'}";
        Map<String, Object> map = Json.fromJsonAsMap(Object.class, Lang.inr(s));
        assertEquals(45, map.get("id"));
        assertEquals("xyz", map.get("name"));
    }

    @Test
    public void testSimpleMap2() {
        String s = "{f:false,t:true,H:30}";
        Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr(s));
        assertTrue((Boolean) map.get("t"));
        assertFalse((Boolean) map.get("f"));
        assertEquals(30, map.get("H"));
    }

    @Test
    public void testSimpleMap3() {
        String s = "{ary:[1,2],t:true,H:30}";
        Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr(s));
        List<?> list = (List<?>) map.get("ary");
        assertEquals(2, list.size());
        assertTrue((Boolean) map.get("t"));
        assertEquals(30, map.get("H"));
    }

    @Test
    public void testSimpleMap4() {
        String s = "{id:45,name:'',txt:\"\"}";
        Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr(s));
        assertEquals(45, map.get("id"));
        assertEquals("", map.get("name"));
        assertEquals("", map.get("txt"));
    }

    @Test
    public void testMap() throws FileNotFoundException {
        Map<String, Object> map = Json.fromJson(HashMap.class,
                                                getFileAsInputStreamReader("org/nutz/json/map.txt"));
        assertEquals("value1", map.get("a1"));
        assertEquals(35, map.get("a2"));
        assertEquals((double) 4.7, map.get("a3"));
        Map<?, ?> m1 = (Map<?, ?>) map.get("m1");
        assertEquals(12, m1.get("x"));
        assertEquals(45, m1.get("y"));
        Map<?, ?> m12 = (Map<?, ?>) m1.get("m12");
        assertEquals("haha", m12.get("w1"));
        assertEquals("fuck", m12.get("w2"));
        Map<?, ?> m2 = (Map<?, ?>) map.get("m2");
        assertEquals("good", m2.get("today"));
        assertEquals("nice", m2.get("tomy"));
    }

    @Test
    public void testSimplePersonObject() throws Exception {
        Person p = Json.fromJson(Person.class,
                                 getFileAsInputStreamReader("org/nutz/json/simplePerson.txt"));
        assertEquals("youoo", p.getName());
        assertEquals("YouChunSheng", p.getRealname());
        assertEquals(69, p.getAge());

        Calendar cal = Calendar.getInstance();
        cal.setTime(p.getBirthday());
        assertEquals(1940, cal.get(Calendar.YEAR));
        assertEquals(7, cal.get(Calendar.MONTH));
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Ignore
    @Test
    public void testPersonObject() throws Exception {
        Person p = Json.fromJson(Person.class,
                                 getFileAsInputStreamReader("org/nutz/json/person.txt"));
        StringBuilder sb = new StringBuilder();
        Writer w = new OutputStreamWriter(new StringOutputStream(sb));
        w.write(p.dump());
        w.write("\n");
        w.write(p.getFather().dump());
        w.write("\n");
        w.write(p.getCompany().getName());
        w.write("\n");
        w.write(p.getCompany().getCreator().dump());
        w.flush();
        w.close();

        assertTrue(Streams.equals(new StringInputStream(sb),
                                  getClass().getResourceAsStream("/org/nutz/json/person.expect.txt")));
    }

    @Test
    public void testSimpleArray() throws Exception {
        String[] expAry = {"abc", "bbc", "fff"};
        String s = String.format("[%s]", Lang.concatBy("\"%s\"", ',', expAry));
        String[] reAry = Json.fromJson(String[].class, Lang.inr(s));
        assertTrue(Arrays.equals(expAry, reAry));
    }

    @Test
    public void testSimpleArray2() throws Exception {
        String[] expAry = {"abc", "bbc", "fff"};
        String s = String.format("[%s]", Lang.concatBy("\"%s\"", ',', expAry));
        String[] reAry = Json.fromJsonAsArray(String.class, Lang.inr(s));
        assertTrue(Arrays.equals(expAry, reAry));
    }

    @Test
    public void testSimpleList() throws Exception {
        String[] expAry = {"abc", "bbc", "fff"};
        String s = String.format("[%s]", Lang.concatBy("\"%s\"", ',', expAry));
        List<String> reAry = Json.fromJsonAsList(String.class, Lang.inr(s));
        assertTrue(Arrays.equals(expAry, reAry.toArray(new String[0])));
    }

    @Test
    public void test_parse_simple_empty_array() throws Exception {
        Object[] objs = Json.fromJson(Object[].class, "[]");
        assertEquals(0, objs.length);
    }

    @Test
    public void testSimpleArraySingleInteger() throws Exception {
        String s = "[2]";
        int[] ary = Json.fromJson(int[].class, Lang.inr(s));
        assertEquals(1, ary.length);
        assertEquals(2, ary[0]);
    }

    @Test
    public void testSimpleArraySingleDate() throws Exception {
        String s = "[\"2008-8-1\"]";
        java.sql.Date[] ary = Json.fromJson(java.sql.Date[].class, Lang.inr(s));
        assertEquals(1, ary.length);
        Calendar cal = Calendar.getInstance();
        cal.setTime(ary[0]);
        assertEquals(2008, cal.get(Calendar.YEAR));
        assertEquals(7, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testSimpleArraySingleObject() throws Exception {
        String s = "[{\"id\":24,\"name\":\"RRR\"}]";
        Abc[] ary = Json.fromJson(Abc[].class, Lang.inr(s));
        assertEquals(1, ary.length);
        assertEquals(24, ary[0].id);
        assertEquals("RRR", ary[0].name);
    }

    @Test
    public void testSimpleObjectArray() throws Exception {
        String s = "[{\"id\":3,\"name\":\"A\"},{\"id\":10,\"name\":\"B\"}]";
        Abc[] ary = Json.fromJson(Abc[].class, Lang.inr(s));
        assertEquals(2, ary.length);
        assertEquals(3, ary[0].id);
        assertEquals(10, ary[1].id);
        assertEquals("A", ary[0].name);
        assertEquals("B", ary[1].name);
    }

    @Test
    public void testNiceModeSimple() throws Exception {
        String s = "{id:45,name:\"x{y:12,t:'yzy'}z\"}";
        Abc abc = Json.fromJson(Abc.class, Lang.inr(s));
        assertEquals(45, abc.id);
        assertEquals("x{y:12,t:'yzy'}z", abc.name);

        s = "{id:45,name:'\"X\"'}";
        abc = Json.fromJson(Abc.class, Lang.inr(s));
        assertEquals(45, abc.id);
        assertEquals("\"X\"", abc.name);
    }

    @Test
    public void testParseNullFieldObject() throws Exception {
        Person p = Json.fromJson(Person.class,
                                 getFileAsInputStreamReader("org/nutz/json/personNull.txt"));
        assertEquals("youoo", p.getName());
        assertEquals("YouChunSheng", p.getRealname());
        assertEquals(69, p.getAge());

        Calendar cal = Calendar.getInstance();
        cal.setTime(p.getBirthday());
        assertEquals(1940, cal.get(Calendar.YEAR));
        assertEquals(7, cal.get(Calendar.MONTH));
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testPrintJsonObject() throws Exception {
        Person p = Json.fromJson(Person.class,
                                 getFileAsInputStreamReader("org/nutz/json/person.txt"));
        String json = Json.toJson(p, JsonFormat.nice());
        Person p2 = Json.fromJson(Person.class, Lang.inr(json));
        assertEquals(p.getName(), p2.getName());
        assertEquals(p.getRealname(), p2.getRealname());
        assertEquals(p.getAge(), p2.getAge());
        assertEquals(p.getBirthday(), p2.getBirthday());
        assertEquals(p.getFather().getName(), p2.getFather().getName());
        assertEquals(p.getFather().getRealname(), p2.getFather().getRealname());
        assertEquals(p.getFather().getAge(), p2.getFather().getAge());
        assertEquals(p.getFather().getBirthday(), p2.getFather().getBirthday());
        assertEquals(p.getCompany().getName(), p2.getCompany().getName());
        assertEquals(p.getCompany().getCreator().getName(), p2.getCompany().getCreator().getName());
        assertEquals(p.getCompany().getCreator().getRealname(), p2.getCompany()
                                                                  .getCreator()
                                                                  .getRealname());
        assertEquals(p.getCompany().getCreator().getAge(), p2.getCompany().getCreator().getAge());
        assertEquals(p.getCompany().getCreator().getFather(), p2.getCompany()
                                                                .getCreator()
                                                                .getFather());
        assertEquals(p.getCompany().getCreator().getBirthday(), p2.getCompany()
                                                                  .getCreator()
                                                                  .getBirthday());
    }

    @Test
    public void testFilterField() throws Exception {
        Person p = Json.fromJson(Person.class,
                                 getFileAsInputStreamReader("org/nutz/json/person.txt"));
        String json = Json.toJson(p, JsonFormat.nice().setActived("^name$"));
        Person p2 = Json.fromJson(Person.class, Lang.inr(json));
        assertEquals(p.getName(), p2.getName());
        assertNull(p2.getRealname());
        assertNull(p2.getBirthday());
        assertNull(p2.getFather());
        assertNull(p2.getCompany());
        assertEquals(0, p2.getAge());
    }

    @Test
    public void testFilterField2() throws Exception {
        Person p = Json.fromJson(Person.class,
                                 getFileAsInputStreamReader("org/nutz/json/person.txt"));
        String json = Json.toJson(p, JsonFormat.nice().setLocked("realname|father|company"));
        Person p2 = Json.fromJson(Person.class, Lang.inr(json));
        assertNull(p2.getRealname());
        assertEquals(p.getName(), p2.getName());
        assertEquals(p.getAge(), p2.getAge());
        assertEquals(p.getBirthday(), p2.getBirthday());
    }

    public static class Project {
        public int id;
        public String name;
        public String alias;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Project) {
                Project p = (Project) obj;
                return id == p.id && name.equals(p.name) && alias.equals(p.alias);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int id = this.id;
            if (name != null)
                id += name.hashCode();
            if (alias != null)
                id += alias.hashCode();
            return id;
        }
    }

    @Test
    public void testOutpuProjectsAsList() throws Exception {
        Project p = new Project();
        p.id = 1;
        p.name = "nutz";
        p.alias = "Nutz Framework";
        Project p2 = Json.fromJson(Project.class, Json.toJson(p));
        assertTrue(p.equals(p2));
    }

    @Test
    public void testUndefined() throws Exception {
        String exp = "{id:45,name:'GG',alias:undefined}";
        Project p = Json.fromJson(Project.class, Lang.inr(exp));
        assertEquals(45, p.id);
        assertEquals("GG", p.name);
        assertNull(p.alias);
    }

    public static class X {
        public int id;
        public XT type;
    }

    public static enum XT {
        A, B
    }

    @Test
    public void testEnumOutput() throws Exception {
        X x = new X();
        x.id = 5;
        x.type = XT.B;
        X x2 = Json.fromJson(X.class, Json.toJson(x));
        assertEquals(x.id, x2.id);
        assertEquals(x.type, x2.type);
    }

    @Test
    public void testEmptyMap() throws Exception {
        Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr("{}"));
        assertEquals(0, map.size());
        map = (Map<?, ?>) Json.fromJson(Lang.inr("  {/*rrrrrrrr*/   }"));
        assertEquals(0, map.size());
    }

    @Test
    public void testEmptyObject() throws Exception {
        X x = Json.fromJson(X.class, Lang.inr("{}"));
        assertEquals(0, x.id);
        assertNull(x.type);
    }

    @Test
    public void test_output_not_quote_name() {
        Base b = Base.make("Red");
        String json = Json.toJson(b, JsonFormat.compact().setQuoteName(false));
        Base b2 = Json.fromJson(Base.class, json);
        assertEquals(b.getCountryId(), b2.getCountryId());
        assertEquals(b.getLevel(), b2.getLevel());
        assertEquals(b.getName(), b2.getName());
    }

    static class A {
        List<String> list1;
        List<String> list2;
    }

    @Test
    public void testDuplicateArrayList() {
        A a = new A();
        a.list1 = new ArrayList<String>();
        a.list1.add("aaa");
        a.list2 = new ArrayList<String>();
        a.list2.add("aaa");
        String json = Json.toJson(a, JsonFormat.compact().setQuoteName(false));
        String exp = "{list1:[\"aaa\"],list2:[\"aaa\"]}";
        assertEquals(exp, json);
    }

    @Test
    public void test_special_char() {
        String s = "\\|\n|\r|\t";
        String exp = "\"\\\\|\\n|\\r|\\t\"";
        assertEquals(exp, Json.toJson(s));
        assertEquals(s, Json.fromJson(exp));
    }

    @Test
    public void test_number_output() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "123");
        String re = Json.toJson(map, JsonFormat.compact().setQuoteName(false));
        assertEquals("{a:\"123\"}", re);
    }

    @Test
    public void test_dollar_as_name() {
        Map<String, Object> map = (Map<String, Object>) Json.fromJson("{$a:-23,b:-2.7}");
        Integer i = (Integer) map.get("$a");
        assertEquals(-23, i.intValue());
        Double d = (Double) map.get("b");
        assertEquals(-2.7, d.floatValue(), 3);
    }

    private InputStreamReader getFileAsInputStreamReader(String fileName) {
        if (!fileName.startsWith("/"))
            fileName = "/" + fileName;
        return new InputStreamReader(getClass().getResourceAsStream(fileName));
    }

    @Test
    public void test_output_json_string() {
        assertEquals("\"A:\\\"'\\\\\"", Json.toJson("A:\"'\\"));
    }

    @Test
    public void test_generic_type_list() {
        String s = "{persons: [{name:'zzh'}, {name:'wendal'}]}";
        Room room = Json.fromJson(Room.class, s);
        assertEquals(2, room.getPersons().size());
        assertEquals("zzh", room.getPersons().get(0).getName());
        assertEquals("wendal", room.getPersons().get(1).getName());
    }

    @Test
    public void test_ioc_value() {
        String s = "{value:1,type:'normal'}";
        IocValue iv = Json.fromJson(IocValue.class, s);
        assertEquals(1, ((Integer) iv.getValue()).intValue());
        assertEquals("normal", iv.getType());
    }

    public static class TFAMWLV {
        Map<String, List<String>> map;
    }

    @Test
    public void test_field_as_map_with_list_value() {
        String str = "{map:{a:['A1','A2'],b:['B1','B2']}}";
        TFAMWLV obj = Json.fromJson(TFAMWLV.class, str);
        assertEquals("B2", obj.map.get("b").get(1));
    }

    @Test
    public void test_output_nostr_key_map() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(22, "hello");
        assertEquals("{\"22\":\"hello\"}", Json.toJson(map, JsonFormat.compact()));
    }

    @Test
    public void test_separator() {
        String str = "Nutz";
        assertEquals("\"Nutz\"", Json.toJson(str, JsonFormat.compact().setSeparator('\"')));
        assertEquals("'Nutz'", Json.toJson(str, JsonFormat.compact().setSeparator('\'')));
    }

    @Test
    public void test_setvalue_by_setter() {
        Person p = Json.fromJson(Person.class, "{num:1}");
        assertEquals(2, p.getNum());
    }

    @Test
    public void test_toJson() {
        Object pc = OuterClass.make();
        assertEquals("ItMe", Json.toJson(pc));
    }

    @Test
    public void test_X() {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("abc", "abc中文abc");
        JsonFormat format = new JsonFormat(true);
        format.setAutoUnicode(true);
        assertEquals("{\"abc\":\"abc\\u4E2D\\u6587abc\"}", Json.toJson(map, format));
    }

    @Test
    public void test_toList() {
        List<Map<String, Integer>> msgList = Json.fromJson(List.class, "[{'a':1}, {'b':2}]");
        assertNotNull(msgList);
        assertTrue(msgList.size() == 2);
        assertEquals(1, msgList.get(0).get("a").intValue());
        assertEquals(2, msgList.get(1).get("b").intValue());
    }

    @Test(timeout = 5000, expected = Throwable.class)
    public void test_bad_json() {
        // Json.fromJson(LinkedHashMap.class,
        // "{persons: [{name:'zzh'}, {name:'wendal'}]");
        // Json.fromJson(LinkedHashMap.class,
        // "{persons: [{name:'zzh'}, {name:'wendal'}}");
        // Json.fromJson(LinkedHashMap.class,
        // "{persons: [{name:'zzh'}, {name'wendal'}]}");
        // Json.fromJson(LinkedHashMap.class,
        // "{persons: [{name:'zzh', {name:'wendal'}]}");
        // Json.fromJson(LinkedHashMap.class,
        // "{persons: [{name:'zzh'}, {name:wendal'}]}");
        Json.fromJson(LinkedHashMap.class, "{persons: [123,,,,,]}");
    }

    @Test
    public void test_render_char() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("charX", 'c');
        assertEquals("{\"charX\":\"c\"}", Json.toJson(map, JsonFormat.compact()));
    }

    @Test
    // For issue 474
    public void test_inner_class() {
        JC c = new JC();
        String str = Json.toJson(c);
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(str);
        assertEquals(1, map.get("ixx").get("abc"));
    }

    // For issue 487
    @Test
    public void test_map_null() {
        String j = "{map:{map:null,m2:{abc:123}}}";
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(j);
        assertNull(map.get("map").get("map"));
    }

    @Test
    public void test_from_list() {
        List<Abc> list = (List<Abc>) Json.fromJson(NutType.list(Abc.class),
                                                   Streams.fileInr("org/nutz/json/list.txt"));
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("nutz", list.get(0).name);
        assertEquals("wendal", list.get(1).name);
    }

    @Test
    public void test_sp() {
        String j = "{'abc':'http:\\/\\/wendal.net'}";
        Map<String, Object> map = Json.fromJson(Map.class, j);
        assertEquals("http://wendal.net", map.get("abc"));
    }

    // zozoh@2012-09-14:去掉，让 Json 更轻薄一些
    // @Test
    // public void test_by() {
    // TestBy b = new TestBy();
    // b.setId(1000);
    // Map<String, Object> map = Json.fromJson(Map.class, Json.toJson(b));
    // assertEquals(1000, map.get("id"));
    // assertEquals("I am OK", map.get("obj"));
    // assertEquals("Wendal", map.get("obj2"));
    // }

    // TODO @Test <- zozoh: 这个用例是不对的，下次如果我看到这个函数，我将删掉它
    // #184
    public void test_setting() {
        String j = "{name2:'abc'}";
        JENObj jj = Json.fromJson(JENObj.class, j);
        assertEquals("abc", jj.getName());
    }

    public static String justOK(Object obj) {
        return "I am OK";
    }

    // zozoh@2012-09-14:去掉，让 Json 更轻薄一些
    // @Test
    // public void test_createBy() {
    // String str = "{children: [{name :'wendal'}]}";
    // MapTreeNode node = Json.fromJson(MapTreeNode.class, str);
    // System.out.println(Json.toJson(node));
    // System.out.println(node.getChildren().get(0).getClass());
    // }
    
    @Test
    public void test_json3() {
        File f = Files.findFile("org/nutz/json/x.json");
        Map<String, Object> map = Json.fromJsonFile(Map.class, f);
        assertEquals(3, map.size());
        System.out.println(map.keySet());
        assertTrue(map.containsKey("dao"));
        
        String str = "{rs:{ok:true,},yes:true}";
        map = Json.fromJson(Map.class, str);
        assertEquals(2, map.size());
        assertEquals(map.get("yes"), true);
        
        str = "{rs:[1,2,3,],yes:true}";
        map = Json.fromJson(Map.class, str);
        assertEquals(2, map.size());
        assertEquals(map.get("yes"), true);
        assertEquals(3, ((List<Integer>)map.get("rs")).get(2).intValue());
    }
}
