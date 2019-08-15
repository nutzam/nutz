package org.nutz.mvc.impl.contextCollector;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewContextCollector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * session变量收集
 */
public class SessionCollector implements ViewContextCollector {
    @Override
    public Context collect(HttpServletRequest req, Object obj) {
        try {
            HttpSession session = Mvcs.getHttpSession(false);
            if (session != null) {
                Map<String, Object> session_attr = new HashMap<String, Object>();
                for (Enumeration<String> en = session.getAttributeNames(); en.hasMoreElements();) {
                    String tem = en.nextElement();
                    session_attr.put(tem, session.getAttribute(tem));
                }
                return Lang.context("session_attr", session_attr);
            }
        }
        catch (Throwable e) {
            // noop
        }

        return null;

    }
}
