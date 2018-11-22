package org.nutz.json.handler;

import java.io.IOException;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonClassHandler extends JsonTypeHandler {

    @Override
    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return mirror.getType() == Class.class;
    }

    @Override
    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj != null && obj instanceof Class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        r.string2Json(((Class) currentObj).getName());
    }

    @Override
    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return Lang.loadClass(String.valueOf(obj));
    }
}
