package org.nutz.json.handler;

import java.io.IOException;
import java.lang.reflect.Type;

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
public class JsonMirrorHandler implements JsonTypeHandler {

    public boolean supportFromJson(Type type) {
        return type == Mirror.class;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj != null && obj instanceof Mirror;
    }

    @SuppressWarnings("rawtypes")
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        r.string2Json(((Mirror) currentObj).getType().getName());
    }

    public Object fromJson(Object data, Type type) throws Exception {
        return Mirror.me(Lang.loadClass(String.valueOf(data)));
    }

    @Override
    public boolean shallCheckMemo() {
        return false;
    }
}
