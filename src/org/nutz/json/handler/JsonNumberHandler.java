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
public class JsonNumberHandler implements JsonTypeHandler {

    public boolean supportFromJson(Type type) {
        return Mirror.me(type).isNumber();
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return Mirror.me(obj).isNumber();
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        String tmp = currentObj.toString();
        if (tmp.equals("NaN")) {
            // TODO 怎样才能应用上JsonFormat中是否忽略控制呢?
            // 因为此时已经写入了key:
            r.writeRaw("null");
        } else
            r.writeRaw(tmp);
    }

    @Override
    public Object fromJson(Object data, Type type) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean shallCheckMemo() {
        return false;
    }
}
