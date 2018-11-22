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
public class JsonMirrorHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return mirror.getType() == Mirror.class;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj != null && obj instanceof Mirror;
    }

    @SuppressWarnings("rawtypes")
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        r.string2Json(((Mirror) currentObj).getType().getName());
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return Mirror.me(Lang.loadClass(String.valueOf(obj)));
    }
}
