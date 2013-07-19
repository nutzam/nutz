package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;

public class Object2Object extends Castor<Object, Object> {

    @Override
    public Object cast(Object src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        try {
            return Mirror.me(toType).born(src);
        } catch (Exception e) {
            return Json.fromJson(toType, Json.toJson(src));
        }
    }

}
