package org.nutz.json.handler;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;

import org.nutz.castor.Castors;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonArrayHandler extends JsonTypeHandler {

    @Override
    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return mirror.isArray();
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
    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return Castors.me().castTo(obj, mirror.getType());
    }

    @Override
    public boolean shallCheckMemo() {
        return true;
    }
}
