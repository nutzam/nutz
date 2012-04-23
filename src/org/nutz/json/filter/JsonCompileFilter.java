package org.nutz.json.filter;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.nutz.json.JsonCompile;
import org.nutz.json.JsonFilter;

/**
 * 扩充 JsonCompile 实现, 以使它支持对JSON字符串的过滤处理.
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonCompileFilter extends JsonCompile{
    private JsonFilter filter;
    public Object parse(Reader reader, List<String> mates, boolean type) {
        filter = new JsonFilter(mates, type);
        return super.parse(reader);
    }
    public Object parse(Reader reader, JsonFilter filter) {
        this.filter = filter;
        return super.parse(reader);
    }
    
    /**
     * 在Map解释添加过滤
     */
    protected void parseMapItem(Map<String, Object> map) throws IOException {
        String key = fetchKey();
        filter.pushPath(key);
        Object val = parseFromHere();
        if(filter.include()){
            map.put(key, val);
        }
        filter.pollPath();
    }
}
