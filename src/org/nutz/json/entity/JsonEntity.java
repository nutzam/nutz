package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nutz.json.JsonException;
import org.nutz.json.JsonField;
import org.nutz.json.JsonFormat;
import org.nutz.json.ToJson;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;
import org.nutz.lang.born.BorningException;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Callback3;

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

    private Map<String, Integer> typeParams; // 如果本类型是范型，存放范型标识的下标
    
    private Method toJsonMethod;

    public JsonEntity(Mirror<?> mirror) {
        // 处理范型
        Type type = mirror.getActuallyType();
        typeParams = new HashMap<String, Integer>();
        if (type instanceof ParameterizedType) {
            ParameterizedType pmType = (ParameterizedType) type;
            int i = 0;
            for (Type pmA : pmType.getActualTypeArguments()) {
                typeParams.put(pmA.toString(), i++);
            }
        }
        // 开始解析
        Field[] flds = mirror.getFields();
        fields = new ArrayList<JsonEntityField>(flds.length);
        for (Field fld : flds) {

            JsonEntityField ef = JsonEntityField.eval(mirror, fld);
            if (null == ef) {
                continue;
            }
            fields.add(ef);
            fieldMap.put(ef.getName(), ef);
        }
        for (Method m : mirror.getMethods()) {
            final JsonField jf = m.getAnnotation(JsonField.class);
            // 忽略方法
            if (null == jf || jf.ignore())
                continue;

            // 如果有，尝试作新的 Entity
            final Method method = m;
            Callback<Method> whenError = new Callback<Method>() {
                // 给定方法即不是 getter 也不是 setter，靠！玩我!
                public void invoke(Method m) {
                    throw Lang.makeThrow(JsonException.class,
                                         "JsonField '%s' should be getter/setter pair!",
                                         m);
                }
            };
            Callback3<String, Method, Method> whenOk = new Callback3<String, Method, Method>() {
                public void invoke(String name, Method getter, Method setter) {
                    // 防止错误
                    if (null == getter || null == setter || Strings.isBlank(name)) {
                        throw Lang.makeThrow(JsonException.class,
                                             "JsonField '%s' should be getter/setter pair!",
                                             method);
                    }
                    // 加入字段表
                    JsonEntityField ef = JsonEntityField.eval(Strings.sBlank(jf.value(), name),
                                                              getter,
                                                              setter);
                    fields.add(ef);
                    fieldMap.put(ef.getName(), ef);
                }
            };
            Mirror.evalGetterSetter(m, whenOk, whenError);
        }

        try {
            borning = mirror.getBorning();
        }
        catch (BorningException e) {
            err = e;
        }
        
        Class<? extends Object> klass = mirror.getType();
        ToJson tj = klass.getAnnotation(ToJson.class);
        String myMethodName = Strings.sNull(null == tj ? null : tj.value(), "toJson");
        try {
            /*
             * toJson()
             */
            try {
                Method myMethod = klass.getMethod(myMethodName);
                if (!myMethod.isAccessible())
                    myMethod.setAccessible(true);
                toJsonMethod = myMethod;
            }
            /*
             * toJson(JsonFormat fmt)
             */
            catch (NoSuchMethodException e1) {
                try {
                    Method myMethod = klass.getMethod(myMethodName, JsonFormat.class);
                    if (!myMethod.isAccessible())
                        myMethod.setAccessible(true);
                    toJsonMethod = myMethod;
                }
                catch (NoSuchMethodException e) {}
            }
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
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
    
    public Method getToJsonMethod() {
        return toJsonMethod;
    }

}
