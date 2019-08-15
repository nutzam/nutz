package org.nutz.mvc.impl.contextCollector;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.ViewContextCollector;
import org.nutz.mvc.impl.processor.ViewProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * 收集返回值
 */
public class ReturnCollector implements ViewContextCollector {
    @Override
    public Context collect(HttpServletRequest req, Object obj) {
        if (null != obj) {
            return Lang.context(ViewProcessor.DEFAULT_ATTRIBUTE, obj);
        }
        return null;
    }
}
