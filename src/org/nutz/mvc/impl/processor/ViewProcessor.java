package org.nutz.mvc.impl.processor;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.view.ViewWrapper;
import org.nutz.mvc.view.VoidView;

public class ViewProcessor extends AbstractProcessor {

    protected View view;
    public static final String DEFAULT_ATTRIBUTE = "obj";
    private static final Log log = Logs.get();
    
    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        view = evalView(config, ai, ai.getOkView());
    }

    public void process(ActionContext ac) throws Throwable {
        Object re = ac.getMethodReturn();
        Object err = ac.getError();
        if (re != null && re instanceof View) {
            if (re instanceof ViewWrapper)
                putRequestAttribute(ac.getRequest(), ((ViewWrapper)re).getData());
            ((View) re).render(ac.getRequest(), ac.getResponse(), err);
        } else {
            putRequestAttribute(ac.getRequest(), null == re ? err : re);
            view.render(ac.getRequest(), ac.getResponse(), null == re ? err : re);
        }
        doNext(ac);
    }
    
    /**
     * 保存对象到attribute
     */
    public static void putRequestAttribute(HttpServletRequest req, Object re){
        if (null != re){
            if(re instanceof Context){
                Context context = (Context) re;
                for(String key : context.keys()){
                    req.setAttribute(key, context.get(key));
                }
            } else {
                req.setAttribute(ViewProcessor.DEFAULT_ATTRIBUTE, re);
            }
        }
    }

    protected static View evalView(NutConfig config, ActionInfo ai, String viewType) {
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
        
        //需要特别提醒一下使用jsonView,但方法的返回值是String的!!
        if("json".equals(type) && String.class.equals(ai.getMethod().getReturnType())) {
            log.warn("Not a good idea : Return String ,and using JsonView!! (Using @Ok(\"raw\") or return map/list/pojo)--> " + Lang.simpleMetodDesc(ai.getMethod()));
        }
        
        for (ViewMaker maker : ai.getViewMakers()) {
            View view = maker.make(config.getIoc(), type, value);
            if (null != view)
                return view;
        }
        throw Lang.makeThrow("Can not eval %s(\"%s\") View for %s", viewType, str, ai.getMethod());
    }
}
