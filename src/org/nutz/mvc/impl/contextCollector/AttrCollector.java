package org.nutz.mvc.impl.contextCollector;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.ViewContextCollector;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象的属性列表
 */
public class AttrCollector implements ViewContextCollector {
    @Override
    public Context collect(HttpServletRequest req, Object obj) {
        Map<String, Object> req_attr = new HashMap<String, Object>();
        for (Enumeration<String> en = req.getAttributeNames(); en.hasMoreElements();) {
            String tem = en.nextElement();
            if (!tem.startsWith("$"))
                req_attr.put(tem, req.getAttribute(tem));
        }
        Context ctx = Lang.context();
        ctx.set("a", req_attr);// 兼容最初的写法
        ctx.set("req_attr", req_attr);
        return ctx;
    }
}
