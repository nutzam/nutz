package org.nutz.lang.inject;

import java.lang.reflect.Method;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class InjectBySetter implements Injecting {
    
    private static final Log log = Logs.get();
    
    private Method setter;
    private Class<?> valueType;

    public InjectBySetter(Method setter) {
        this.setter = setter;
        valueType = setter.getParameterTypes()[0];
    }

    public void inject(Object obj, Object value) {
        Object v = null;
        try {
            v = Castors.me().castTo(value, valueType);
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