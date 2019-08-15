package org.nutz.mvc.impl.contextCollector;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.ViewContextCollector;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 收集请求参数
 */
public class ParamCollector implements ViewContextCollector {
    @Override
    public Context collect(HttpServletRequest req, Object obj) {
        Map<String, String> p = new HashMap<String, String>();
        Context ctx = Lang.context();
        for (Object o : Lang.enum2collection(req.getParameterNames(), new ArrayList<String>())) {
            String key = (String) o;
            String value = req.getParameter(key);
            p.put(key, value);
            ctx.set(key, value);// 以支持直接获取请求参数
        }
        ctx.set("p", p);
        return ctx;
    }
}
