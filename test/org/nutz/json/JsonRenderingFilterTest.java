package org.nutz.json;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.json.JsonFormat.FilterType;

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
        JsonFormat format = new JsonFormat();
        format.setMates(mates);
        format.setFilterType(FilterType.include);
        String s = Json.toJson(a, format);
        assertEquals(s, "{\"name\":\"juqkai\"}");
    }
    @Test
    public void excludeTest(){
        List<String> mates = new ArrayList<String>();
        mates.add("name");
        Abc a = new Abc(1, "juqkai");
        JsonFormat format = new JsonFormat();
        format.setMates(mates);
        format.setFilterType(FilterType.exclude);
        String s = Json.toJson(a, format);
        assertEquals(s, "{\"id\":1}");
    }
}
