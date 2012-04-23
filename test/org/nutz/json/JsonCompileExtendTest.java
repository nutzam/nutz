package org.nutz.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.nutz.lang.Streams;

/**
 * JsonCompileExtend测试
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonCompileExtendTest {
    List<String> mates = new ArrayList<String>();
    @Before
    public void init(){
        
    }
    @Test
    public void test1(){
        mates.add("age");
        Map<?, ?> map = (Map<?, ?>) Json.fromJson(Streams.fileInr("org/nutz/json/person.txt"), mates, true);
        assertEquals(1, map.size());
    }
}
