package org.nutz.json.handler;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonArrayHandler implements JsonTypeHandler {

    @Override
    public boolean supportFromJson(Type type) {
        return false;
    }

    @Override
    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return mirror.isArray();
    }

    @Override
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        Writer writer = r.getWriter();
        writer.append('[');
        int len = Array.getLength(currentObj) - 1;
        if (len > -1) {
            int i;
            for (i = 0; i < len; i++) {
                r.render(Array.get(currentObj, i));
                r.appendPairEnd();
                writer.append(' ');
            }
            r.render(Array.get(currentObj, i));
        }
        writer.append(']');
    }

    @Override
    public Object fromJson(Object data, Type type) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        return false;
    }
}
