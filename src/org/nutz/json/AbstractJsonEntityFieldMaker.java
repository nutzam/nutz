package org.nutz.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Mirror;

public abstract class AbstractJsonEntityFieldMaker implements JsonEntityFieldMaker {

    @Override
    public List<JsonEntityField> make(Mirror<?> mirror) {
        Field[] flds = mirror.getFields(true, false);
        List<JsonEntityField> fields = new ArrayList<JsonEntityField>(flds.length);
        for (Field fld : flds) {
            JsonEntityField ef = make(mirror, fld);
            if (null != ef)
                fields.add(ef);
        }
        for (Method m : mirror.getMethods()) {
            JsonEntityField ef = make(mirror, m);
            if (null != ef)
                fields.add(ef);
        }
        return fields;
    }

    public abstract JsonEntityField make(Mirror<?> mirror, Field field);

    public JsonEntityField make(Mirror<?> mirror, Method method) {
        return null;
    }

}