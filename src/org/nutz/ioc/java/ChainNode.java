package org.nutz.ioc.java;

import org.nutz.ioc.IocMaking;
import org.nutz.lang.Lang;

public abstract class ChainNode {

    private ChainNode next;

    public void setNext(ChainNode next) {
        this.next = next;
    }

    protected abstract Object getValue(IocMaking ing, Object obj) throws Exception;

    protected abstract String asString();

    public Object eval(IocMaking ing) {
        return eval(ing, null);
    }

    private Object eval(IocMaking ing, Object obj) {
        try {
            Object v = getValue(ing, obj);
            if (null == next)
                return v;
            return next.eval(ing, v);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(asString());
        if (null != next)
            sb.append('.').append(next.toString());
        return sb.toString();
    }

}
