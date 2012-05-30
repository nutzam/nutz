package org.nutz.maplist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.maplist.impl.MapListCell;
import org.nutz.maplist.impl.convert.FilterConvertImpl;

/**
 * MapList测试
 * @author juqkai(juqkai@gmail.com)
 */
public class MapListTest {
    
    /**
     * 测试MAP提取
     */
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
     * 过滤测试
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
     * 过滤测试
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
}
