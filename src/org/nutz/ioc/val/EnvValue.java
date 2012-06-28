package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class EnvValue implements ValueProxy {

    private String name;

    public EnvValue(String name) {
        this.name = name;
    }

    public Object get(IocMaking ing) {
        return System.getenv(name);
    }

}
