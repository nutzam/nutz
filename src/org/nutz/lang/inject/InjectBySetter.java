package org.nutz.lang.inject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.conf.NutConf;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class InjectBySetter implements Injecting {
    
    private static final Log log = Logs.get();
    
    protected FastMethod fm;
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
            if (NutConf.USE_FASTCLASS) {
                if (fm == null)
                    fm = FastClassFactory.get(setter);
                fm.invoke(obj, v);
            } else {
                setter.invoke(obj, v);
            }
        }
        catch (Exception _e) {
            Throwable e = _e;
            if (e instanceof InvocationTargetException)
                e = ((InvocationTargetException)e).getTargetException();
            if (log.isInfoEnabled())
                log.info("Fail to value by setter", e);
            throw Lang.wrapThrow(e, "Fail to set '%s'[ %s ] by setter %s.'%s()' because [%s]: %s",
                                    value,
                                    v == null ? value : v,
                                    setter.getDeclaringClass().getName(),
                                    setter.getName(),
                                    Lang.unwrapThrow(e),
                                    Lang.unwrapThrow(e).getMessage());
        }
    }

}