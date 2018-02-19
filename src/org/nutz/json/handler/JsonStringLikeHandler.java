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
public class JsonStringLikeHandler implements JsonTypeHandler {

    public boolean supportFromJson(Type type) {
        return Mirror.me(type).isStringLike() || Mirror.me(type).isChar();
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return mirror.isStringLike() || mirror.isChar();
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        r.string2Json(String.valueOf(currentObj));
    }

    @Override
    public Object fromJson(Object data, Type type) throws Exception {
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        return false;
    }

}
