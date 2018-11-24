package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;
import org.nutz.lang.Mirror;

public class FieldNode extends ChainNode {

    private String name;

    public FieldNode(String name) {
        this.name = name;
    }

    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        return Mirror.me(obj.getClass()).getValue(obj, name);
    }

    protected String asString() {
        return name;
    }

}
