package org.nutz.maplist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.json.Abc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Streams;
import org.nutz.lang.stream.StringReader;

/**
 * MapList测试
 * @author juqkai(juqkai@gmail.com)
 */
public class MapListTest {
    
    /**
     * 测试MAP提取
     */
    @Test
    public void cellTest(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/person.txt"));
        assertEquals("dtri", Maplist.cell(dest, "company.name"));
        assertEquals("Dao", Maplist.cell(dest, "company.creator.name"));
    }
    /**
     * 测试提取LIST下的值
     */
    @Test
    public void cellArrayTest(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertEquals("1", Maplist.cell(dest, "users[0].name"));
        assertEquals("2", Maplist.cell(dest, "people[1].name"));
    }
    /**
     * 测试提取LIST下的值
     */
    @Test
    public void cellArrayTest1(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertEquals("1", Maplist.cell(dest, "users.0.name"));
        assertEquals("2", Maplist.cell(dest, "people.1.name"));
    }
    /**
     * 测试提取LIST
     */
    @Test
    public void cellArrayTest2(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertTrue(Maplist.cell(dest, "users") instanceof List);
        List<?> list = (List<?>)Maplist.cell(dest, "users");
        assertEquals(2, list.size());
    }
    /**
     * 测试根就是LIST的情况
     */
    @Test
    public void cellArrayTest3(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertTrue(Maplist.cell(dest, "users") instanceof List);
        List<?> list = (List<?>)Maplist.cell(dest, "users");
        assertEquals("1", Maplist.cell(list, "[0].name"));
    }
    /**
     * 包含过滤测试
     */
    @Test
    public void includeFilterConvertTest(){
        List<String> paths = new ArrayList<String>();
        paths.add("users[].name");
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = Maplist.includeFilter(dest, paths);
        assertNull(Maplist.cell(obj, "people"));
        assertEquals("1", Maplist.cell(obj, "users[0].name"));
    }
    /**
     * 排除过滤测试
     */
    @Test
    public void excludeFilterConvertTest(){
        List<String> paths = new ArrayList<String>();
        paths.add("users[].name");
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = Maplist.excludeFilter(dest, paths);
        assertNull(Maplist.cell(obj, "users[0].name"));
        assertEquals(12, Maplist.cell(obj, "users[0].age"));
    }
    /**
     * 排除过滤测试
     */
    @Test
    public void excludeFilterConvertTest2(){
        List<String> paths = new ArrayList<String>();
        paths.add("users");
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = Maplist.excludeFilter(dest, paths);
        assertNull(Maplist.cell(obj, "users"));
        assertEquals(12, Maplist.cell(obj, "people[0].age"));
    }
    
    /**
     * 对象转MapList测试
     */
    @Test
    public void objCompileTest(){
        Abc abc = new Abc();
        abc.id = 1;
        abc.name = "jk";
        Object obj = Maplist.toMaplist(abc);
        assertTrue(obj instanceof Map);
        assertEquals(1, Maplist.cell(obj, "id"));
        assertEquals("jk", Maplist.cell(obj, "name"));
    }
    /**
     * 对象转MapList测试
     */
    @Test
    public void objCompileArrayTest(){
        Abc abc = new Abc();
        abc.id = 1;
        abc.name = "jk";
        Abc b = new Abc();
        b.id = 2;
        b.name = "juqkai";
        List<Abc> list = new ArrayList<Abc>();
        list.add(abc);
        list.add(b);
        
        Object obj = Maplist.toMaplist(list);
        assertTrue(obj instanceof List);
        assertEquals(1, Maplist.cell(obj, "[0].id"));
        assertEquals("juqkai", Maplist.cell(obj, "1.name"));
    }
    
    /**
     * 对象转MapList循环引用测试
     */
    @Test
    public void objCompileCircularReferenceTest(){
        A a = new A();
        B b = new B();
        a.b = b;
        b.a = a;
        
        Object obj = Maplist.toMaplist(a);
        assertTrue(obj instanceof Map);
        assertNotNull(Maplist.cell(obj, "b"));
        assertEquals("b", Maplist.cell(obj, "b.name"));
        assertEquals("a", Maplist.cell(obj, "b.a.name"));
        assertEquals(Maplist.cell(obj, "b.a.b"), Maplist.cell(obj, "b"));
    }
    
    
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 结构转换测试
     */
    /**
     * 简单转换
     */
    @Test
    public void structureConvertSimple(){
        String json = "{'name':'jk', 'age':12}";
        String model = "{'name':'姓名', 'age':'年龄'}";
        String dest = "{\"姓名\":\"jk\",\"年龄\":12}";
        Object obj = Maplist.convert(Json.fromJson(new StringReader(json)), new StringReader(model));
        assertEquals("jk", Maplist.cell(obj, "姓名"));
        assertEquals(12, Maplist.cell(obj, "年龄"));
        assertEquals(dest, Json.toJson(obj, new JsonFormat()));
    }
    
    /**
     * 数组转换
     */
    @Test
    public void structureConvertSimpleArray(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':'user[].姓名', 'age':'user[].年龄'}]}";
        Object obj = Maplist.convert(Json.fromJson(new StringReader(json)), new StringReader(model));
        assertEquals("jk", Maplist.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Maplist.cell(obj, "user[1].姓名"));
        assertEquals(12, Maplist.cell(obj, "user[0].年龄"));
        assertEquals(5, Maplist.cell(obj, "user[1].年龄"));
    }
    
    /**
     * 多路径转换
     */
    @Test
    public void structureConvertMultiPath(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]}";
        Object obj = Maplist.convert(Json.fromJson(new StringReader(json)), new StringReader(model));
        assertEquals("jk", Maplist.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Maplist.cell(obj, "user[1].姓名"));
        assertEquals("jk", Maplist.cell(obj, "people[0].name"));
        assertEquals(5, Maplist.cell(obj, "people[1].age"));
    }
    
    /**
     * 根路径为Array的转换
     */
    @Test
    public void structureConvertRoot2Array(){
        String json = "[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]";
        String model = "[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]";
        String dest = "{\"people\":[{\"age\":12,\"name\":\"jk\"}, {\"age\":5,\"name\":\"nutz\"}],\"user\":[{\"姓名\":\"jk\",\"年龄\":12}, {\"姓名\":\"nutz\",\"年龄\":5}]}";
        Object obj = Maplist.convert(Json.fromJson(new StringReader(json)), new StringReader(model));
        assertEquals("jk", Maplist.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Maplist.cell(obj, "user[1].姓名"));
        assertEquals("jk", Maplist.cell(obj, "people[0].name"));
        assertEquals(5, Maplist.cell(obj, "people[1].age"));
        assertEquals(dest, Json.toJson(obj, new JsonFormat()));
    }
    /**
     * Array转换成根array结构
     */
    @Test
    public void structureConvertArray2Root(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['[].name'], 'age':'[].age'}]}";
        String dest = "[{\"age\":12,\"name\":\"jk\"}, {\"age\":5,\"name\":\"nutz\"}]";
        Object obj = Maplist.convert(Json.fromJson(new StringReader(json)), new StringReader(model));
        assertEquals("jk", Maplist.cell(obj, "[0].name"));
        assertEquals(5, Maplist.cell(obj, "[1].age"));
        assertEquals(dest, Json.toJson(obj, new JsonFormat()));
    }

}
