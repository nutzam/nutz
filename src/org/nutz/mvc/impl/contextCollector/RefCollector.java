package org.nutz.mvc.impl.contextCollector;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewContextCollector;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 被转换过的值
 */
public class RefCollector implements ViewContextCollector {
    @Override
    public Context collect(HttpServletRequest req, Object obj) {
        Object ref = Mvcs.getActionContext().getReferObject();
        if(ref == null) {
            return null;
        }
        Map<String, Object> p = new HashMap<String, Object>();
        if (ref instanceof Map) {
            p.putAll((Map) ref);
        } else {
            p.put("$ref", ref);
        }
        Context ctx = Lang.context();
        ctx.set("r", p);
        return ctx;
    }
}
