package org.nutz.json.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.nutz.json.JsonFilter;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRendering;

/**
 * 转换JSON过滤器
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonRenderingFilter extends JsonRendering{
    private JsonFilter filter;
    public JsonRenderingFilter(Writer writer, JsonFormat format, List<String> mates, boolean type) {
        super(writer, format);
        filter = new JsonFilter(mates, type);
    }
    public JsonRenderingFilter(Writer writer, JsonFormat format, JsonFilter filter) {
        super(writer, format);
        this.filter = filter;
    }
    
    protected boolean appendPair(boolean first, String name, Object value) throws IOException {
        boolean type = false;
        filter.pushPath(name);
        if(filter.include()){
            super.appendPair(first, name, value);
            type = true;
        }
        filter.pollPath();
        return type;
    }
}
