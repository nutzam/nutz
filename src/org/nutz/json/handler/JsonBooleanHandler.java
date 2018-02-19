package org.nutz.json.handler;

import java.io.IOException;
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
public class JsonBooleanHandler implements JsonTypeHandler {

    public boolean supportFromJson(Type type) {
        return Mirror.me(type).isBoolean();
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return mirror.isBoolean();
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        r.writeRaw(String.valueOf(currentObj));
    }

    public Object fromJson(Object data, Type type) throws Exception {
        return Boolean.valueOf(String.valueOf(data));
    }

    @Override
    public boolean shallCheckMemo() {
        return false;
    }
}
