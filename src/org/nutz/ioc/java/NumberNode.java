package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;
import org.nutz.json.Json;

public class NumberNode extends ChainNode {

    private Object v;

    public NumberNode(String num) {
        v = Json.fromJson(num);
    }

    @Override
    protected String asString() {
        return v.toString();
    }

    @Override
    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        return v;
    }

}
