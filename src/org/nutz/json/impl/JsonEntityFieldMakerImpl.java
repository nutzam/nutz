package org.nutz.json.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nutz.json.AbstractJsonEntityFieldMaker;
import org.nutz.json.JsonException;
import org.nutz.json.JsonField;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Callback3;

/**
 * DefaultJsonEntityFieldMaker
 *
 * @author 幸福的旁边(happyday517@163.com)
 */
public class JsonEntityFieldMakerImpl extends AbstractJsonEntityFieldMaker {

    @Override
    public JsonEntityField make(Mirror<?> mirror, Field field) {
        return JsonEntityField.eval(mirror, field);
    }

    @Override
    public JsonEntityField make(final Mirror<?> mirror, final Method method) {
        final JsonField jf = method.getAnnotation(JsonField.class);
        // 忽略方法
        if (null == jf || jf.ignore())
            return null;
        final JsonEntityField[] result = new JsonEntityField[1];
        // 如果有，尝试作新的 Entity
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
                JsonEntityField ef = JsonEntityField.eval(mirror, Strings.sBlank(jf.value(), name),
                                                          getter,
                                                          setter);
                result[0] = ef;
            }
        };
        Mirror.evalGetterSetter(method, whenOk, whenError);
        return result[0];
    }

}