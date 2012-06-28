package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;

public class StringNode extends ChainNode {

    private String s;

    public StringNode(String s) {
        this.s = s;
    }

    @Override
    protected String asString() {
        return "'" + s + "'";
    }

    @Override
    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        return s;
    }

}
