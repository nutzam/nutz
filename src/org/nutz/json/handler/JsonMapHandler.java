package org.nutz.json.handler;

import java.io.IOException;
import java.util.Map;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonMapHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return mirror.isMap();
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return mirror.isMap();
    }

    @SuppressWarnings("rawtypes")
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        r.map2Json((Map) currentObj);
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        return true;
    }
}
