package org.nutz.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.lang.Objs;
import org.nutz.lang.stream.StringReader;

/**
 * JsonConvert测试
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonConvertTest {
    
    /**
     * 简单转换
     */
    @Test
    public void testSimple(){
        String json = "{'name':'jk', 'age':12}";
        String model = "{'name':'姓名', 'age':'年龄'}";
        Object obj = Json.convertJson(new StringReader(json), new StringReader(model));
        assertEquals("jk", Objs.cell(obj, "姓名"));
        assertEquals(12, Objs.cell(obj, "年龄"));
    }
    
    /**
     * 数组转换
     */
    @Test
    public void testSimpleArray(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':'user[].姓名', 'age':'user[].年龄'}]}";
        Object obj = Json.convertJson(new StringReader(json), new StringReader(model));
        assertEquals("jk", Objs.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Objs.cell(obj, "user[1].姓名"));
        assertEquals(12, Objs.cell(obj, "user[0].年龄"));
        assertEquals(5, Objs.cell(obj, "user[1].年龄"));
    }
    
    /**
     * 多路径转换
     */
    @Test
    public void testMultiPath(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]}";
        Object obj = Json.convertJson(new StringReader(json), new StringReader(model));
        assertEquals("jk", Objs.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Objs.cell(obj, "user[1].姓名"));
        assertEquals("jk", Objs.cell(obj, "people[0].name"));
        assertEquals(5, Objs.cell(obj, "people[1].age"));
    }
    
    /**
     * 根路径为Array的转换
     */
    @Test
    public void testRoot2Array(){
        String json = "[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]";
        String model = "[{'name':['user[].姓名', 'people[].name'], 'age':['user[].年龄', 'people[].age']}]";
        Object obj = Json.convertJson(new StringReader(json), new StringReader(model));
        assertEquals("jk", Objs.cell(obj, "user[0].姓名"));
        assertEquals("nutz", Objs.cell(obj, "user[1].姓名"));
        assertEquals("jk", Objs.cell(obj, "people[0].name"));
        assertEquals(5, Objs.cell(obj, "people[1].age"));
    }
    /**
     * Array转换成根array结构
     */
    @Test
    public void testArray2Root(){
        String json = "{'user':[{'name':'jk', 'age':12},{'name':'nutz', 'age':5}]}";
        String model = "{'user':[{'name':['[].name'], 'age':'[].age'}]}";
        Object obj = Json.convertJson(new StringReader(json), new StringReader(model));
        assertEquals("jk", Objs.cell(obj, "[0].name"));
        assertEquals(5, Objs.cell(obj, "[1].age"));
    }
}
