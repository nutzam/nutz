package org.nutz.castor.castor;

import java.lang.reflect.Method;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Mirror;

public class Object2String extends Castor<Object, String> {

    @Override
    public String cast(Object src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        for (Method method : Mirror.me(src).getMethods()) {
            if ("toString".equals(method.getName())) {
                return src.toString();
            }
        }
        return Json.toJson(src, JsonFormat.tidy());
    }

}
