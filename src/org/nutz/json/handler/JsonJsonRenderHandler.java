package org.nutz.json.handler;

import java.io.IOException;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 支持 JsonRender
 */
public class JsonJsonRenderHandler extends JsonTypeHandler {

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj != null && obj instanceof JsonRender;
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        ((JsonRender) currentObj).render(null);
    }
}
