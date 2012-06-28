package org.nutz.mvc.impl;

import javax.servlet.ServletContext;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.meta.IocValue;
import org.nutz.ioc.val.StaticValue;
import org.nutz.lang.Lang;

public class ServletValueProxyMaker implements ValueProxyMaker {

    private ServletContext sc;

    public ServletValueProxyMaker(ServletContext sc) {
        this.sc = sc;
    }

    public String[] supportedTypes() {
        return Lang.array("app");
    }

    public ValueProxy make(IocMaking ing, IocValue iv) {
        String value = iv.getValue().toString();
        if ("app".equals(iv.getType())) {
            if ("$servlet".equalsIgnoreCase(value))
                return new StaticValue(sc);
            return new StaticValue(sc.getAttribute(value));
        }
        return null;
    }

}
