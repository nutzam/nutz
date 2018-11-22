package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class IocSelfValue implements ValueProxy {

    public Object get(IocMaking ing) {
        return ing.getIoc();
    }

}
