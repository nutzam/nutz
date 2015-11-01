package org.nutz.mvc.impl.processor;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.view.ViewWrapper;
import org.nutz.mvc.view.ViewZone;

public class ViewProcessor extends AbstractProcessor {

    protected View view;
    public static final String DEFAULT_ATTRIBUTE = "obj";
    private static final Log log = Logs.get();
    protected int index = -1;
    
    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        //需要特别提醒一下使用jsonView,但方法的返回值是String的!!
        if("json".equals(ai.getOkView()) && String.class.equals(ai.getMethod().getReturnType())) {
            log.warn("Not a good idea : Return String ,and using JsonView!! (Using @Ok(\"raw\") or return map/list/pojo)--> " + Lang.simpleMetodDesc(ai.getMethod()));
        }
        view = evalView(config, ai, ai.getOkView());

        Class<?>[] params = ai.getMethod().getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (params[i].isAssignableFrom(ViewModel.class)) {
                index = i;
                break;
            }
        }
        if (view instanceof ViewZone)
            ((ViewZone)view).setIndex(index);
    }

    public void process(ActionContext ac) throws Throwable {
        Object re = ac.getMethodReturn();
        Object err = ac.getError();
        if (re != null && re instanceof View) {
            if (re instanceof ViewWrapper)
                putRequestAttribute(ac.getRequest(), ((ViewWrapper)re).getData());
            ((View) re).render(ac.getRequest(), ac.getResponse(), err);
        } else {
            if (index > -1 && re == null && err == null) {
                re = ac.getMethodArgs()[index];
            }
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

    public static View evalView(NutConfig config, ActionInfo ai, String viewType) {
        return ViewZone.makeView(config, ai, viewType, true);
    }
}
