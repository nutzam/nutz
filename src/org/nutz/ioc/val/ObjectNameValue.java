package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class ObjectNameValue implements ValueProxy {

    public Object get(IocMaking ing) {
        return ing.getObjectName();
    }

}
