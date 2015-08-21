package org.nutz.lang.inject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class InjectBySetter implements Injecting {
    
    private static final Log log = Logs.get();
    
    private Method setter;
    private Class<?> valueType;
    private Type type;
    private boolean isMapCollection;

    public InjectBySetter(Method setter) {
        this.setter = setter;
        valueType = setter.getParameterTypes()[0];
        type = setter.getGenericParameterTypes()[0];
        isMapCollection = Map.class.isAssignableFrom(valueType) ||
                       Collection.class.isAssignableFrom(valueType);
    }

    public void inject(Object obj, Object value) {
        Object v = null;
        try {
            if (isMapCollection && value != null && value instanceof String) {
                v = Json.fromJson(type, value.toString());
            } else {
                v = Castors.me().castTo(value, valueType);
            }
            setter.invoke(obj, v);
        }
        catch (Exception e) {
            if (log.isInfoEnabled())
                log.info("Fail to value by setter", e);
            throw Lang.makeThrow(    "Fail to set '%s'[ %s ] by setter %s.'%s()' because [%s]: %s",
                                    value,
                                    v,
                                    setter.getDeclaringClass().getName(),
                                    setter.getName(),
                                    Lang.unwrapThrow(e),
                                    Lang.unwrapThrow(e).getMessage());
        }
    }

}