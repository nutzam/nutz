package org.nutz.ioc.trigger;

import java.lang.reflect.Method;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.lang.Lang;

public class MethodEventTrigger implements IocEventTrigger<Object> {

    private Method method;

    public MethodEventTrigger(Method method) {
        this.method = method;
    }

    public void trigger(Object obj) {
        try {
            method.invoke(obj);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

}
