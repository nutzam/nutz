package org.nutz.json.handler;

import java.io.IOException;

import org.nutz.castor.Castors;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal
 *
 */
public class JsonNumberHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return mirror.isNumber();
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
    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return Castors.me().castTo(obj, mirror.getType());
    }
}
