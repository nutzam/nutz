package org.nutz.json;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 合并测试
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class JsonMergeTest {
    
    @Test
    public void simpleTest(){
        String json1 = "{'user':'jk'}";
        String json2 = "{'age':12}";
        String json3 = "{'user':'jk','age':12}";
        
        Object obj = Json.fromJsonMerge(json1, json2);
        Object obj2 = Json.fromJson(json3);
        assertEquals(obj, obj2);
    }
    
    @Test
    public void mapMergeTest(){
        String json1 = "{'map':{'name':'jk', 'age':12, 'a':'b'}}";
        String json2 = "{'map':{'name':'nutz', 'age':13}}";
        String json3 = "{'map':{'name':'nutz', 'age':13, 'a':'b'}}";
        
        Object obj = Json.fromJsonMerge(json1, json2);
        Object obj2 = Json.fromJson(json3);
        assertEquals(obj, obj2);
    }
    
    @Test
    public void listMergeTest(){
        String json1 = "{'list':[{'name':'jk', 'age':12, 'a':'b'}]}";
        String json2 = "{'list':[{'name':'nutz', 'age':13}]}";
        String json3 = "{'list':[{'name':'jk', 'age':12, 'a':'b'}, {'name':'nutz', 'age':13}]}";
        
        Object obj = Json.fromJsonMerge(json1, json2);
        Object obj2 = Json.fromJson(json3);
        assertEquals(obj, obj2);
    }
    
    @Test
    public void listMapMergeTest(){
        String json1 = "{'name':'test', map: {'key':'jk',list:[1,2,3]}}";
        String json2 = "{'name':'jsonMerge', map: {'key':'jk',list:[9,8,7]}, 'abc':'haha'}";
        String json3 = "{'name':'jsonMerge', map: {'key':'jk',list:[1,2,3,9,8,7]}, 'abc':'haha'}";
        
        Object obj = Json.fromJsonMerge(json1, json2);
        Object obj2 = Json.fromJson(json3);
        assertEquals(obj, obj2);
    }
}
