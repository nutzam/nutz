package org.nutz.json.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

/**
 * 专门处理Throwable的JSON序列化, 避免在JDK9+上反射访问Throwable私有字段.
 * <p>
 * 输出格式包含异常类名, 消息, 调用栈以及cause(若存在).
 *
 * @author wendal
 */
public class JsonThrowableHandler extends JsonTypeHandler {

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj instanceof Throwable;
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        Throwable e = (Throwable) currentObj;
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("class", e.getClass().getName());
        map.put("message", e.getMessage());
        List<String> stackTrace = new ArrayList<String>();
        for (StackTraceElement ste : e.getStackTrace()) {
            stackTrace.add(String.valueOf(ste));
        }
        map.put("stackTrace", stackTrace);
        Throwable cause = e.getCause();
        if (cause != null && cause != e) {
            map.put("cause", cause);
        }
        r.map2Json(map);
    }

    public boolean shallCheckMemo() {
        return true;
    }
}
