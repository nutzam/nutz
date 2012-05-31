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
import org.nutz.lang.Streams;
import org.nutz.lang.stream.StringReader;
import org.nutz.maplist.impl.MapListCell;
import org.nutz.maplist.impl.compile.ObjCompileImpl;
import org.nutz.maplist.impl.convert.FilterConvertImpl;
import org.nutz.maplist.impl.convert.JsonConvertImpl;
import org.nutz.maplist.impl.convert.StructureConvert;

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
        assertEquals("dtri", MapListCell.cell(dest, "company.name"));
        assertEquals("Dao", MapListCell.cell(dest, "company.creator.name"));
    }
    /**
     * 测试提取LIST下的值
     */
    @Test
    public void cellArrayTest(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertEquals("1", MapListCell.cell(dest, "users[0].name"));
        assertEquals("2", MapListCell.cell(dest, "people[1].name"));
    }
    /**
     * 测试提取LIST下的值
     */
    @Test
    public void cellArrayTest1(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertEquals("1", MapListCell.cell(dest, "users.0.name"));
        assertEquals("2", MapListCell.cell(dest, "people.1.name"));
    }
    /**
     * 测试提取LIST
     */
    @Test
    public void cellArrayTest2(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertTrue(MapListCell.cell(dest, "users") instanceof List);
        List<?> list = (List<?>)MapListCell.cell(dest, "users");
        assertEquals(2, list.size());
    }
    /**
     * 测试根就是LIST的情况
     */
    @Test
    public void cellArrayTest3(){
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        assertTrue(MapListCell.cell(dest, "users") instanceof List);
        List<?> list = (List<?>)MapListCell.cell(dest, "users");
        assertEquals("1", MapListCell.cell(list, "[0].name"));
    }
    /**
     * 包含过滤测试
     */
    @Test
    public void includeFilterConvertTest(){
        List<String> paths = new ArrayList<String>();
        paths.add("users[].name");
        FilterConvertImpl filter = new FilterConvertImpl(paths);
        filter.useIncludeModel();
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = filter.convert(dest);
        assertNull(MapListCell.cell(obj, "people"));
        assertEquals("1", MapListCell.cell(obj, "users[0].name"));
    }
    /**
     * 排除过滤测试
     */
    @Test
    public void excludeFilterConvertTest(){
        List<String> paths = new ArrayList<String>();
        paths.add("users[].name");
        FilterConvertImpl filter = new FilterConvertImpl(paths);
//        filter.useExcludeModel();
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = filter.convert(dest);
        assertNull(MapListCell.cell(obj, "users[0].name"));
        assertEquals(12, MapListCell.cell(obj, "users[0].age"));
    }
    /**
     * 排除过滤测试
     */
    @Test
    public void excludeFilterConvertTest2(){
        List<String> paths = new ArrayList<String>();
        paths.add("users");
        FilterConvertImpl filter = new FilterConvertImpl(paths);
//        filter.useExcludeModel();
        Object dest = Json.fromJson(Streams.fileInr("org/nutz/json/mateList.txt"));
        Object obj = filter.convert(dest);
        assertNull(MapListCell.cell(obj, "users"));
        assertEquals(12, MapListCell.cell(obj, "people[0].age"));
    }
    
    /**
     * 对象转MapList测试
     */
    @Test
    public void objCompileTest(){
        Abc abc = new Abc();
        abc.id = 1;
        abc.name = "jk";
        ObjCompileImpl convert = new ObjCompileImpl();
        Object obj = convert.compile(abc);
        assertTrue(obj instanceof Map);
        assertEquals(1, MapListCell.cell(obj, "id"));
        assertEquals("jk", MapListCell.cell(obj, "name"));
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
        
        ObjCompileImpl compile = new ObjCompileImpl();
        Object obj = compile.compile(list);
        assertTrue(obj instanceof List);
        assertEquals(1, MapListCell.cell(obj, "[0].id"));
        assertEquals("juqkai", MapListCell.cell(obj, "1.name"));
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
        
        ObjCompileImpl compile = new ObjCompileImpl();
        Object obj = compile.compile(a);
        assertTrue(obj instanceof Map);
        assertNotNull(MapListCell.cell(obj, "b"));
        assertEquals("b", MapListCell.cell(obj, "b.name"));
        assertEquals("a", MapListCell.cell(obj, "b.a.name"));
        assertEquals(MapListCell.cell(obj, "b.a.b"), MapListCell.cell(obj, "b"));
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
        StructureConvert convert = new StructureConvert(new StringReader(model));
        Object obj = convert.convert(Json.fromJson(new StringReader(json)));
        assertEquals("jk", MapListCell.cell(obj, "姓名"));
        assertEquals(12, MapListCell.cell(obj, "年龄"));
        assertEquals(dest, new JsonConvertImpl().convert(obj));
    }
    
    /**
     * 数组转换
     */
    @Test
    public void structureConvertSimpleArray(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':'user[].姓名', 'age':'user[].年龄'}]}";
        StructureConvert convert = new StructureConvert(new StringReader(model));
        Object obj = convert.convert(Json.fromJson(new StringReader(json)));
        assertEquals("jk", MapListCell.cell(obj, "user[0].姓名"));
        assertEquals("nutz", MapListCell.cell(obj, "user[1].姓名"));
        assertEquals(12, MapListCell.cell(obj, "user[0].年龄"));
        assertEquals(5, MapListCell.cell(obj, "user[1].年龄"));
    }
    
    /**
     * 多路径转换
     */
    @Test
    public void structureConvertMultiPath(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]}";
        StructureConvert convert = new StructureConvert(new StringReader(model));
        Object obj = convert.convert(Json.fromJson(new StringReader(json)));
        assertEquals("jk", MapListCell.cell(obj, "user[0].姓名"));
        assertEquals("nutz", MapListCell.cell(obj, "user[1].姓名"));
        assertEquals("jk", MapListCell.cell(obj, "people[0].name"));
        assertEquals(5, MapListCell.cell(obj, "people[1].age"));
    }
    
    /**
     * 根路径为Array的转换
     */
    @Test
    public void structureConvertRoot2Array(){
        String json = "[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]";
        String model = "[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]";
        String dest = "{\"people\":[{\"age\":12,\"name\":\"jk\"}, {\"age\":5,\"name\":\"nutz\"}],\"user\":[{\"姓名\":\"jk\",\"年龄\":12}, {\"姓名\":\"nutz\",\"年龄\":5}]}";
        StructureConvert convert = new StructureConvert(new StringReader(model));
        Object obj = convert.convert(Json.fromJson(new StringReader(json)));
        assertEquals("jk", MapListCell.cell(obj, "user[0].姓名"));
        assertEquals("nutz", MapListCell.cell(obj, "user[1].姓名"));
        assertEquals("jk", MapListCell.cell(obj, "people[0].name"));
        assertEquals(5, MapListCell.cell(obj, "people[1].age"));
        assertEquals(dest, new JsonConvertImpl().convert(obj));
    }
    /**
     * Array转换成根array结构
     */
    @Test
    public void structureConvertArray2Root(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['[].name'], 'age':'[].age'}]}";
        String dest = "[{\"age\":12,\"name\":\"jk\"}, {\"age\":5,\"name\":\"nutz\"}]";
        StructureConvert convert = new StructureConvert(new StringReader(model));
        Object obj = convert.convert(Json.fromJson(new StringReader(json)));
        assertEquals("jk", MapListCell.cell(obj, "[0].name"));
        assertEquals(5, MapListCell.cell(obj, "[1].age"));
        assertEquals(dest, new JsonConvertImpl().convert(obj));
    }

}
