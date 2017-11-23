package org.nutz.json.entity;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonEntityFieldMaker;
import org.nutz.json.JsonException;
import org.nutz.json.JsonFormat;
import org.nutz.json.ToJson;
import org.nutz.lang.Lang;
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

    private Map<String, JsonEntityField> fieldMap = new LinkedHashMap<String, JsonEntityField>();

    private Borning<?> borning;

    private BorningException err;

    private Map<String, Integer> typeParams; // 如果本类型是范型，存放范型标识的下标
    
    private Method toJsonMethod;
    
    private JsonEntityFieldMaker fieldMaker;
    
    private JsonCallback jsonCallback;

    public JsonEntity(Mirror<?> mirror) {
        fieldMaker = Json.getDefaultFieldMaker();
        // 处理范型
        Type type = mirror.getActuallyType();
        typeParams = new LinkedHashMap<String, Integer>();
        if (type instanceof ParameterizedType) {
            ParameterizedType pmType = (ParameterizedType) type;
            int i = 0;
            for (Type pmA : pmType.getActualTypeArguments()) {
                typeParams.put(pmA.toString(), i++);
            }
        }
        // 开始解析
        fields = fieldMaker.make(mirror);
        for (JsonEntityField ef : fields)
            fieldMap.put(ef.getName(), ef);

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
        if (toJsonMethod != null) {
            final int paramCount = toJsonMethod.getParameterTypes().length;
            jsonCallback = new JsonCallback() {
                public boolean toJson(Object obj, JsonFormat jf, Writer writer) throws IOException {
                    try {
                        if (paramCount == 0)
                            writer.write((String)toJsonMethod.invoke(obj));
                        else
                            writer.write((String)toJsonMethod.invoke(obj, jf));
                    }
                    catch (Exception e) {
                        throw new JsonException(err);
                    }
                    return true;
                }
                public Object fromJson(Object obj) {
                    return null;
                }
            };
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
    
    public JsonCallback getJsonCallback() {
        return jsonCallback;
    }
    
    public void setJsonCallback(JsonCallback jsonCallback) {
        this.jsonCallback = jsonCallback;
    }
    
    public void setBorning(Borning<?> borning) {
        this.borning = borning;
    }
    
    @Deprecated
    public Method getToJsonMethod() {
        return toJsonMethod;
    }
    
    public Map<String, JsonEntityField> getFieldMap() {
        return fieldMap;
    }
}