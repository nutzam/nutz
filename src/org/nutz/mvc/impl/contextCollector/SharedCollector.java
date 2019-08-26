package org.nutz.mvc.impl.contextCollector;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewContextCollector;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 共享变量收集器
 */
public class SharedCollector implements ViewContextCollector {
    
    protected List<ViewContextCollector> list;
    protected boolean hasItem;

    public Context collect(HttpServletRequest req, Object obj) {
        Ioc ioc = Mvcs.getIoc();
        Context ctx = Lang.context();
        if (ioc == null)
            return ctx;
        if (list == null) {
            List<ViewContextCollector> tmp = new ArrayList<ViewContextCollector>();
            String[] names = ioc.getNamesByType(ViewContextCollector.class);
            for (String name : names) {
                ViewContextCollector vcc = ioc.get(ViewContextCollector.class, name);
                tmp.add(vcc);
                hasItem = true;
            }
            list = tmp;
        }
        if (hasItem) {
            for (ViewContextCollector vcc : list) {
                ctx.putAll(vcc.collect(req, obj));
            }
        }
        return ctx;
    }
}
