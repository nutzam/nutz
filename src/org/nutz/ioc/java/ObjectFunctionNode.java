package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;
import org.nutz.lang.Mirror;

public class ObjectFunctionNode extends ChainNode {

    private String name;
    private ChainNode[] args;

    public ObjectFunctionNode(String name, ChainNode[] args) {
        this.name = name;
        this.args = args;
    }

    @Override
    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        if (null == obj)
            return null;
        Object[] fas = new Object[args.length];
        for (int i = 0; i < args.length; i++)
            fas[i] = args[i].getValue(ing, null);
        return Mirror.me(obj.getClass()).invoke(obj, name, fas);
    }

    protected String asString() {
        StringBuilder sb = new StringBuilder();
        if (args.length > 0) {
            sb.append(args[0].toString());
            for (int i = 1; i < args.length; i++)
                sb.append(", ").append(args[i].toString());
        }
        return String.format("%s(%s)", name, sb);
    }

}
