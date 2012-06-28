package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.Iocs;
import org.nutz.lang.meta.Pair;

public class IocObjectNode extends ChainNode {

    private String name;
    private Class<?> type;

    public IocObjectNode(String name) {
        Pair<Class<?>> p = Iocs.parseName(name);
        this.name = p.getName();
        this.type = p.getValue();
    }

    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        return ing.getIoc().get(type, name);
    }

    @Override
    protected String asString() {
        if (null == type)
            return "$" + name;
        return "$" + name + ":" + type.getName();
    }

}
