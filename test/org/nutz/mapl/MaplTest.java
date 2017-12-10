package org.nutz.mapl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.nutz.json.Abc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.NutType;
import org.nutz.mapl.impl.MaplRebuild;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mvc.adaptor.ParamExtractor;
import org.nutz.mvc.adaptor.Params;
import org.nutz.mvc.adaptor.injector.ObjectNaviNode;

/**
 * MapList测试
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class MaplTest {

    /**
     * Issue #1355
     */
    @Test
    public void test_issue_1355() {
        Object dest;

//        dest = Json.fromJson("{a: ['x',['A','B']]}");
//        assertEquals("x", Mapl.cell(dest, "'a[0]"));
        
        dest = Json.fromJson("{a: [[],['A','B']]}");
        assertEquals("A", Mapl.cell(dest, "'a[1][0]"));

        dest = Json.fromJson("{'a.b': {c:'ABC'}}");
        assertEquals("ABC", Mapl.cell(dest, "'a.b'.c"));
    }

    /**
     * Issue #978
     */
    @Test
    public void cellTestSpecialKey() {
        Object dest = Json.fromJson("{'a.b':'AB', x : [{'c.d':'CD'},{'e.f':'EF'}]}");

        assertEquals("AB", Mapl.cell(dest, "'a.b'"));
        assertEquals("CD", Mapl.cell(dest, "x[0].'c.d'"));
        assertEquals("CD", Mapl.cell(dest, "x.0.'c.d'"));
        assertEquals("EF", Mapl.cell(dest, "x[1].'e.f'"));
        assertEquals("EF", Mapl.cell(dest, "x.1.'e.f'"));

    }

    /**
     * 测试MAP提取
     */
    @Test
    public void cellTest() {
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/person.txt"));
        assertEquals("dtri", Mapl.cell(dest, "company.name"));
        assertEquals("Dao", Mapl.cell(dest, "company.creator.name"));
    }

    /**
     * 测试提取LIST下的值
     */
    @Test
    public void cellArrayTest() {
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertEquals("1", Mapl.cell(dest, "users[0].name"));
        assertEquals("2", Mapl.cell(dest, "people[1].name"));
    }

    /**
     * 测试提取LIST下的值
     */
    @Test
    public void cellArrayTest1() {
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertEquals("1", Mapl.cell(dest, "users.0.name"));
        assertEquals("2", Mapl.cell(dest, "people.1.name"));
    }

    /**
     * 测试提取LIST
     */
    @Test
    public void cellArrayTest2() {
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertTrue(Mapl.cell(dest, "users") instanceof List);
        List<?> list = (List<?>) Mapl.cell(dest, "users");
        assertEquals(2, list.size());
    }

    /**
     * 测试根就是LIST的情况
     */
    @Test
    public void cellArrayTest3() {
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertTrue(Mapl.cell(dest, "users") instanceof List);
        List<?> list = (List<?>) Mapl.cell(dest, "users");
        assertEquals("1", Mapl.cell(list, "[0].name"));
    }

    /**
     * 包含过滤测试
     */
    @Test
    public void includeFilterConvertTest() {
        List<String> paths = new ArrayList<String>();
        paths.add("users[].name");
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = Mapl.includeFilter(dest, paths);
        assertNull(Mapl.cell(obj, "people"));
        assertEquals("1", Mapl.cell(obj, "users[0].name"));
    }

    /**
     * 排除过滤测试
     */
    @Test
    public void excludeFilterConvertTest() {
        List<String> paths = new ArrayList<String>();
        paths.add("users[].name");
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = Mapl.excludeFilter(dest, paths);
        assertNull(Mapl.cell(obj, "users[0].name"));
        assertEquals(12, Mapl.cell(obj, "users[0].age"));
    }

    /**
     * 排除过滤测试
     */
    @Test
    public void excludeFilterConvertTest2() {
        List<String> paths = new ArrayList<String>();
        paths.add("users");
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = Mapl.excludeFilter(dest, paths);
        assertNull(Mapl.cell(obj, "users"));
        assertEquals(12, Mapl.cell(obj, "people[0].age"));
    }

    /**
     * 对象转MapList测试
     */
    @Test
    public void objCompileTest() {
        Abc abc = new Abc();
        abc.id = 1;
        abc.name = "jk";
        Object obj = Mapl.toMaplist(abc);
        assertTrue(obj instanceof Map);
        assertEquals(1, Mapl.cell(obj, "id"));
        assertEquals("jk", Mapl.cell(obj, "name"));
    }

    /**
     * 对象转MapList测试
     */
    @Test
    public void objCompileTest2() {
        A a1 = new A();
        a1.name = "a1";
        A a2 = new A();
        a2.name = "a2";
        B b = new B();
        a2.b = b;
        A a3 = new A();
        a3.name = "a3";

        C c = new C();
        c.mylist = new ArrayList<A>();
        c.mylist.add(a1);
        c.mylist.add(a2);
        c.mylist.add(a3);

        String abcJson = Json.toJson(c);
        Object abc = Json.fromJson(abcJson);
        C c2 = Mapl.maplistToT(abc, C.class);

        assertTrue(c.mylist.size() == c2.mylist.size());
        assertTrue(c.mylist.get(0).name.equals(c2.mylist.get(0).name));
        assertTrue(c.mylist.get(1).name.equals(c2.mylist.get(1).name));
        assertTrue(c.mylist.get(2).name.equals(c2.mylist.get(2).name));
        assertTrue(c.mylist.get(1).b.name.equals(c2.mylist.get(1).b.name));
    }

    /**
     * 对象转MapList测试
     */
    @Test
    public void objCompileArrayTest() {
        Abc abc = new Abc();
        abc.id = 1;
        abc.name = "jk";
        Abc b = new Abc();
        b.id = 2;
        b.name = "juqkai";
        List<Abc> list = new ArrayList<Abc>();
        list.add(abc);
        list.add(b);

        Object obj = Mapl.toMaplist(list);
        assertTrue(obj instanceof List);
        assertEquals(1, Mapl.cell(obj, "[0].id"));
        assertEquals("juqkai", Mapl.cell(obj, "1.name"));
    }

    /**
     * 对象转MapList循环引用测试
     */
    @Test
    public void objCompileCircularReferenceTest() {
        A a = new A();
        B b = new B();
        a.b = b;
        b.a = a;

        Object obj = Mapl.toMaplist(a);
        assertTrue(obj instanceof Map);
        assertNotNull(Mapl.cell(obj, "b"));
        assertEquals("b", Mapl.cell(obj, "b.name"));
        assertEquals("a", Mapl.cell(obj, "b.a.name"));
        assertEquals(Mapl.cell(obj, "b.a.b"), Mapl.cell(obj, "b"));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 结构转换测试
     */

    /**
     * 简单转换
     */
    @Test
    public void structureConvertSimple() {
        String json = "{'name':'jk', 'age':12}";
        String model = "{'name':'姓名', 'age':'年龄'}";
        String dest = "{\"姓名\":\"jk\",\"年龄\":12}";
        Object obj = Mapl.convert(Json.fromJson(Lang.inr(json)), Lang.inr(model));
        assertEquals("jk", Mapl.cell(obj, "姓名"));
        assertEquals(12, Mapl.cell(obj, "年龄"));
        assertEquals(dest, Json.toJson(obj, new JsonFormat()));
    }

    /**
     * 数组转换
     */
    @Test
    public void structureConvertSimpleArray() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':'user[].姓名', 'age':'user[].年龄'}]}";
        Object obj = Mapl.convert(Json.fromJson(Lang.inr(json)), Lang.inr(model));
        assertEquals("jk", Mapl.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Mapl.cell(obj, "user[1].姓名"));
        assertEquals(12, Mapl.cell(obj, "user[0].年龄"));
        assertEquals(5, Mapl.cell(obj, "user[1].年龄"));
    }

    /**
     * 多路径转换
     */
    @Test
    public void structureConvertMultiPath() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]}";
        Object obj = Mapl.convert(Json.fromJson(Lang.inr(json)), Lang.inr(model));
        assertEquals("jk", Mapl.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Mapl.cell(obj, "user[1].姓名"));
        assertEquals("jk", Mapl.cell(obj, "people[0].name"));
        assertEquals(5, Mapl.cell(obj, "people[1].age"));
    }

    /**
     * 根路径为Array的转换
     */
    @Test
    public void structureConvertRoot2Array() {
        String json = "[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]";
        System.out.println(Json.fromJson(json));
        String model = "[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]";
        System.out.println(Json.fromJson(model));
        // String dest = "{\"people\":[{\"age\":12,\"name\":\"jk\"},
        // {\"age\":5,\"name\":\"nutz\"}],\"user\":[{\"姓名\":\"jk\",\"年龄\":12},
        // {\"姓名\":\"nutz\",\"年龄\":5}]}";

        Object obj = Mapl.convert(Json.fromJson(Lang.inr("[{'name':'jk', 'age':12}]")),
                                  Lang.inr(model));
        System.out.println(obj);

        //
        // Object obj = Mapl.convert(Json.fromJson(Lang.inr(json)),
        // Lang.inr(model));
        // System.out.println(obj.getClass());
        // assertEquals("jk", Mapl.cell(obj, "user[0].姓名"));
        // assertEquals("nutz", Mapl.cell(obj, "user[1].姓名"));
        // assertEquals("jk", Mapl.cell(obj, "people[0].name"));
        // assertEquals(5, Mapl.cell(obj, "people[1].age"));
        // assertEquals(dest, Json.toJson(obj, new JsonFormat()));
    }

    /**
     * Array转换成根array结构
     */
    @Test
    public void structureConvertArray2Root() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['[].name'], 'age':'[].age'}]}";
        String dest = "[{\"name\":\"jk\",\"age\":12}, {\"name\":\"nutz\",\"age\":5}]";
        Object obj = Mapl.convert(Json.fromJson(Lang.inr(json)), Lang.inr(model));
        assertEquals("jk", Mapl.cell(obj, "[0].name"));
        assertEquals(5, Mapl.cell(obj, "[1].age"));
        assertEquals(dest, Json.toJson(obj, new JsonFormat()));
    }

    /**
     * 添加元素到mapl中
     */
    @Test
    public void addItemTest() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        Object obj = Json.fromJson(json);
        Mapl.put(obj, "user[0].test", "test");
        assertEquals("test", Mapl.cell(obj, "user[0].test"));
    }

    /**
     * 删除元素到mapl中
     */
    @Test
    public void delItemTest() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        Object obj = Json.fromJson(json);
        Mapl.del(obj, "user[0].age");
        assertNull(Mapl.cell(obj, "user[0].age"));
        assertEquals(1, ((Map<?, ?>) Mapl.cell(obj, "user[0]")).size());
    }

    /**
     * 修改元素到mapl中
     */
    @Test
    public void updateItemTest() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        Object obj = Json.fromJson(json);
        Mapl.update(obj, "user[0].name", "test");
        assertEquals("test", Mapl.cell(obj, "user[0].name"));
    }

    @Test
    public void issue243Test() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        Object obj = Json.fromJson(json);
        Object item = Mapl.cell(obj, "user[]");
        assertFalse(item instanceof List);
        assertTrue(item instanceof Map);
    }

    @Test
    public void issue243Test2() {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map = new HashMap<String, Object>();

        List<String> items = new LinkedList<String>();
        items.add(new String("aa"));
        items.add(new String("bb"));

        map.put("id", 0);
        map.put("items", items);

        list.add(map);

        assertEquals(Mapl.cell(list, "[0].items.0"), "aa");
        assertTrue(Mapl.cell(list, "[0].items[0]").equals("aa"));
        assertTrue(Mapl.cell(list, "[0].items[1]").equals("bb"));
        assertTrue(Mapl.cell(list, "[0].items[]").equals("aa"));
        assertTrue(Mapl.cell(list, "[0].items").equals(items));
    }

    @Test
    public void testIssue322() {
        String json = "{name:'nutz', age:12, address:[{area:1,name:'abc'},{area:2,name:'123'}]}";
        Object obj = Json.fromJson(json);
        Object newobj = Mapl.excludeFilter(obj, Lang.list("age", "address[].area"));
        JsonFormat jf = new JsonFormat(true);
        assertEquals("{\"name\":\"nutz\",\"address\":[{\"name\":\"abc\"}, {\"name\":\"123\"}]}",
                     Json.toJson(newobj, jf));
    }

    /**
     * 排除过滤测试，过滤多个项的内容
     */
    @Test
    public void excludeFilterConvertTest_MultiplePath1() {
        List<String> paths = new ArrayList<String>();
        paths.add("users[].name");
        paths.add("people[].age");
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = Mapl.excludeFilter(dest, paths);
        assertNotNull(Mapl.cell(obj, "users"));
        assertEquals(12, Mapl.cell(obj, "users[0].age"));
        assertEquals("1", Mapl.cell(obj, "people[0].name"));
    }

    /**
     * 排除过滤测试，过滤多个项的内容。这是来自手册上的例子
     */
    @Test
    public void excludeFilterConvertTest_MultiplePath2() {
        String json = "{name:'nutz', age:12, address:[{area:1,name:'abc'},{area:2,name:'123'}]}";
        Object obj = Json.fromJson(json);
        List<String> list = new ArrayList<String>();
        list.add("age");
        list.add("address[].area");
        Object newobj = Mapl.excludeFilter(obj, list);
        assertNull(Mapl.cell(newobj, "age"));
        assertEquals("nutz", Mapl.cell(newobj, "name"));
        assertNull(Mapl.cell(newobj, "address[0].area"));
        assertEquals("abc", Mapl.cell(newobj, "address[0].name"));
    }

    @Test
    public void test_maplrebuild() {
        MaplRebuild req = new MaplRebuild();
        req.put("s1[0]", "test");
        req.put("s2.s2[0]", "test");
        System.out.println(Json.toJson(req.fetchNewobj()));
    }

    @Test
    public void test_complex_prefix() throws Exception {
        String params = "draw=1&columns%5B0%5D%5Bdata%5D=userId&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=loginname&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=nickname&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=asc&start=0&length=10&search%5Bvalue%5D=&search%5Bregex%5D=false";
        // String params =
        // "columns%5B0%5D%5Bdata%5D=userId&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true";
        NutMap map = new NutMap();
        for (String kv : params.split("&")) {
            // System.out.println(kv);
            String[] tmp = kv.split("=");
            String key = URLDecoder.decode(tmp[0], "UTF-8");
            String value = URLDecoder.decode(tmp.length > 1 ? tmp[1] : "", "UTF-8");
            map.put(key, value);
        }
        System.out.println(map);
        String prefix = "columns";
        Object refer = map;
        HttpServletRequest req = new MockHttpServletRequest();

        ObjectNaviNode no = new ObjectNaviNode();
        String pre = "";
        if ("".equals(prefix))
            pre = "node.";
        ParamExtractor pe = Params.makeParamExtractor(req, refer);
        for (Object name : pe.keys()) {
            String na = (String) name;
            if (na.startsWith(prefix)) {
                String[] val = pe.extractor(na);
                no.put(pre + na, val);
            }
        }
        Object model = no.get();
        System.out.println(Json.toJson(model));
        Object re = Mapl.maplistToObj(model, NutType.list(DataTableColumn.class));
        System.out.println(Json.toJson(re));
    }
}
