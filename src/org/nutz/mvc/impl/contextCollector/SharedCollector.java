package org.nutz.mvc.impl.contextCollector;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewContextCollector;

import javax.servlet.http.HttpServletRequest;

/**
 * 共享变量收集器
 */
public class SharedCollector implements ViewContextCollector {
    @Override
    public Context collect(HttpServletRequest req, Object obj) {
        Ioc ioc = Mvcs.getIoc();
        Context ctx = Lang.context();
        String[] names = ioc.getNamesByType(ViewContextCollector.class);
        for (String name : names) {
            ViewContextCollector vcc = ioc.get(ViewContextCollector.class, name);
            ctx.putAll(vcc.collect(req, obj));
        }
        return ctx;
    }
}
