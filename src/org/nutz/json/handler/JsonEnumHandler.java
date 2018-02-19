package org.nutz.json.handler;

import java.io.IOException;
import java.lang.reflect.Type;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonShape;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;

/**
 * 
 * @author wendal
 *
 */
public class JsonEnumHandler implements JsonTypeHandler {

    public boolean supportFromJson(Type type) {
        return Lang.getTypeClass(type).isEnum();
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return mirror.isEnum();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        Mirror mr = Mirror.me(currentObj.getClass());
        // 枚举
        if (mr.isEnum()) {
            JsonShape shape = Mirror.getAnnotationDeep(mr.getType(), JsonShape.class);
            if (shape == null) {
                r.string2Json(((Enum) currentObj).name());
            } else {
                switch (shape.value()) {
                case ORDINAL:
                    r.writeRaw(String.valueOf(((Enum) currentObj).ordinal()));
                    break;
                case OBJECT:
                    NutMap map = Lang.obj2nutmap(currentObj);
                    if (map.isEmpty()) {
                        r.string2Json(((Enum) currentObj).name());
                    } else {
                        r.map2Json(map);
                    }
                    break;
                default:
                    r.string2Json(((Enum) currentObj).name());
                    break;
                }
            }
        }
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
