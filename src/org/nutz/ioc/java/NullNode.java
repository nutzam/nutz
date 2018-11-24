package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;

public class NullNode extends ChainNode {

    protected String asString() {
        return "null";
    }

    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        return null;
    }

}
