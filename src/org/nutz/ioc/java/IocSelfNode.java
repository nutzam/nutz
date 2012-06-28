package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;

public class IocSelfNode extends ChainNode {

    @Override
    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        return ing.getIoc();
    }

    @Override
    protected String asString() {
        return "@Ioc";
    }

}
