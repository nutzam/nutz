package org.nutz.maplist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.maplist.impl.MapListCell;
import org.nutz.maplist.impl.convert.FilterConvertImpl;

import static org.junit.Assert.*;

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
        assertTrue(obj instanceof Map);
        
        Map<?,?> map = (Map<?, ?>) obj;
        assertNull(map.get("people"));
        
        List<?> list = (List<?>) map.get("users");
        assertEquals("1", ((Map<?,?>)list.get(0)).get("name"));
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
        assertTrue(obj instanceof Map);
        
        Map<?,?> map = (Map<?, ?>) obj;
        List<?> list = (List<?>) map.get("users");
        Map<?, ?> map2 = ((Map<?,?>)list.get(0));
        assertNull(map2.get("name"));
        assertEquals(12, map2.get("age"));
    }
}
