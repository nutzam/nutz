package org.nutz.json.handler;

import java.io.IOException;
import java.lang.reflect.Type;
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
public class JsonMapHandler implements JsonTypeHandler {

    public boolean supportFromJson(Type type) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj instanceof Map;
    }

    @SuppressWarnings("rawtypes")
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        r.map2Json((Map) currentObj);
    }

    public Object fromJson(Object data, Type type) throws Exception {
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        return true;
    }
}
