package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class StaticValue implements ValueProxy {

    private Object obj;

    public StaticValue(Object obj) {
        this.obj = obj;
    }

    public Object get(IocMaking ing) {
        return obj;
    }

}
