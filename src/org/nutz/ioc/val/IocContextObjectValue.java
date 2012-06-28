package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class IocContextObjectValue implements ValueProxy {

    public Object get(IocMaking ing) {
        return ing.getContext();
    }

}
