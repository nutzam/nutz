package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.meta.IocObject;

public class InnerValue implements ValueProxy {

    private IocObject iobj;

    public InnerValue(IocObject iobj) {
        this.iobj = iobj;
    }

    public Object get(IocMaking ing) {
        IocMaking innering = ing.clone(null);
        ObjectProxy op = ing.getObjectMaker().make(innering, iobj);
        return op.get(iobj.getType(), innering);
    }

}
