package org.nutz.json.handler;

import java.io.IOException;
import java.lang.reflect.Type;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 支持 JsonRender
 */
public class JsonJsonRenderHandler implements JsonTypeHandler {

    public boolean supportFromJson(Type type) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj != null && obj instanceof JsonRender;
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        ((JsonRender) currentObj).render(null);
    }

    public Object fromJson(Object data, Type type) throws Exception {
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        return false;
    }
}
