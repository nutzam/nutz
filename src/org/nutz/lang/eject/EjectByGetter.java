package org.nutz.lang.eject;

import java.lang.reflect.Method;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class EjectByGetter implements Ejecting {

    private static final Log log = Logs.get();

    private Method getter;

    public EjectByGetter(Method getter) {
        this.getter = getter;
    }

    public Object eject(Object obj) {
        try {
            return null == obj ? null : getter.invoke(obj);
        }
        catch (Exception e) {
            if (log.isInfoEnabled())
                log.info("Fail to value by getter", e);
            throw Lang.makeThrow(    "Fail to invoke getter %s.'%s()' because [%s]: %s",
                                    getter.getDeclaringClass().getName(),
                                    getter.getName(),
                                    Lang.unwrapThrow(e),
                                    Lang.unwrapThrow(e).getMessage());
        }
    }

}
