package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;
import org.nutz.lang.born.BorningException;

/**
 * 记录一个Java如何映射 JSON 字符串的规则
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JsonEntity {

    private List<JsonEntityField> fields;
    
    private Map<String, JsonEntityField> fieldMap = new HashMap<String, JsonEntityField>();

    private Borning<?> borning;

    private BorningException err;

    public JsonEntity(Mirror<?> mirror) {
        Field[] flds = mirror.getFields();
        fields = new ArrayList<JsonEntityField>(flds.length);
        Set<String> names = new HashSet<String>();
        for (Field fld : flds) {
            JsonEntityField ef = JsonEntityField.eval(mirror, fld);
            if (null == ef) {
                names.add(fld.getName());
                continue;
            }
            if (names.add(ef.getName())) {
                fields.add(ef);
                fieldMap.put(ef.getName(), ef);
            }
        }
        for (Method method : mirror.getMethods()) {
            String methodName = method.getName();
            if (methodName.length() > 3 && methodName.startsWith("set") && method.getParameterTypes().length == 1) {
                String name = Strings.lowerFirst(methodName.substring(3));
                if (names.add(name)) {
                    JsonEntityField ef = JsonEntityField.eval(mirror, method);
                    fields.add(ef);
                    fieldMap.put(ef.getName(), ef);
                }
            }
        }

        try {
            borning = mirror.getBorning();
        }
        catch (BorningException e) {
            err = e;
        }
    }

    public List<JsonEntityField> getFields() {
        return fields;
    }

    public Object born() {
        if (null == borning)
            throw err;
        return borning.born(new Object[0]);
    }

    public JsonEntityField getField(String name) {
        return fieldMap.get(name);
    }
}
