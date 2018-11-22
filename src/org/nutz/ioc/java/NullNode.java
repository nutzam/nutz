package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;

public class NullNode extends ChainNode {

    @Override
    protected String asString() {
        return "null";
    }

    @Override
    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        return null;
    }

}
