package org.nutz.mapl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.json.Abc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mapl.Mapl;

/**
 * MapList测试
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class MaplTest {

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
        String model = "[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]";
        String dest = "{\"people\":[{\"age\":12,\"name\":\"jk\"}, {\"age\":5,\"name\":\"nutz\"}],\"user\":[{\"姓名\":\"jk\",\"年龄\":12}, {\"姓名\":\"nutz\",\"年龄\":5}]}";
        Object obj = Mapl.convert(Json.fromJson(Lang.inr(json)), Lang.inr(model));
        assertEquals("jk", Mapl.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Mapl.cell(obj, "user[1].姓名"));
        assertEquals("jk", Mapl.cell(obj, "people[0].name"));
        assertEquals(5, Mapl.cell(obj, "people[1].age"));
        assertEquals(dest, Json.toJson(obj, new JsonFormat()));
    }

    /**
     * Array转换成根array结构
     */
    @Test
    public void structureConvertArray2Root() {
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['[].name'], 'age':'[].age'}]}";
        String dest = "[{\"age\":12,\"name\":\"jk\"}, {\"age\":5,\"name\":\"nutz\"}]";
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
        assertEquals("{\"address\":[{\"name\":\"abc\"}, {\"name\":\"123\"}],\"name\":\"nutz\"}",
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

}
