package org.nutz.mvc.view;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.ViewMaker2;
import org.nutz.mvc.impl.processor.ViewProcessor;

public class ViewZone implements View {
    
    private static final Log log = Logs.get();
    
    protected ActionInfo ai;
    
    protected View dft;
    
    protected NutConfig config;
    
    protected int index = -1;

    public ViewZone(NutConfig config, ActionInfo ai, View dft) {
        super();
        this.config = config;
        this.ai = ai;
        this.dft = dft;
        Method m = ai.getMethod();
        if (log.isInfoEnabled() && m.getReturnType().equals(Void.class)) {
            log.info("using resp View but return void!");
        }
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
        if (obj == null)
            dft.render(req, resp, obj);
        else {
            View v = makeView(config, ai, obj.toString(), false);
            if (index > -1) {
                Object re = Mvcs.getActionContext().getMethodArgs()[index];
                ViewProcessor.putRequestAttribute(req, re);
                v.render(req, resp, re);
            } else {
                v.render(req, resp, null);
            }
        }
    }
    
    public static View makeView(NutConfig config, ActionInfo ai, String viewType, boolean allowProxy) {
        if (Strings.isBlank(viewType))
            return new VoidView();

        String str = viewType;
        int pos = str.indexOf(':');
        String type, value;
        if (pos > 0) {
            type = Strings.trim(str.substring(0, pos).toLowerCase());
            value = Strings.trim(pos >= (str.length() - 1) ? null : str.substring(pos + 1));
        } else {
            type = str;
            value = null;
        }
        
        if (allowProxy && "re".equals(type)) {
            View dft = null;
            if (value != null)
                dft = makeView(config, ai, value, false);
            return new ViewZone(config, ai, dft);
        }
        
        for (ViewMaker maker : ai.getViewMakers()) {
            if (maker instanceof ViewMaker2) {
                View view = ((ViewMaker2)maker).make(config, ai, type, value);
                if (view != null)
                    return view;
            }
            View view = maker.make(config.getIoc(), type, value);
            if (null != view)
                return view;
        }
        throw Lang.makeThrow("Can not eval %s(\"%s\") View for %s", viewType, str, ai.getMethod());
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
}
