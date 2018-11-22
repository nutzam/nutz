package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class IocContextObjectValue implements ValueProxy {

    @Override
    public Object get(IocMaking ing) {
        return ing.getContext();
    }

}
