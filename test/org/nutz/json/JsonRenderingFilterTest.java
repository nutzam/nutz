package org.nutz.json;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonRenderingFilterTest {
    
    @Test
    public void includeTest(){
        List<String> mates = new ArrayList<String>();
        mates.add("name");
        Abc a = new Abc(1, "juqkai");
        JsonFilter filter = new JsonFilter(mates, true);
        String s = Json.toJson(a, filter);
        assertEquals(s, "{\n   \"name\" :\"juqkai\"\n}");
    }
    @Test
    public void excludeTest(){
        List<String> mates = new ArrayList<String>();
        mates.add("name");
        Abc a = new Abc(1, "juqkai");
        JsonFilter filter = new JsonFilter(mates, false);
        String s = Json.toJson(a, filter);
        assertEquals(s, "{\n   \"id\" :1\n}");
    }
}
