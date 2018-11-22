package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.impl.ActionInvoker;

public class ActionHandler {

    private Loading loading;

    private UrlMapping mapping;

    private NutConfig config;

    public ActionHandler(NutConfig config) {
        this.config = config;
        this.loading = config.createLoading();
        this.mapping = loading.load(config);
    }

    public boolean handle(HttpServletRequest req, HttpServletResponse resp) {
        ActionContext ac = new ActionContext();
        ac.setRequest(req).setResponse(resp).setServletContext(config.getServletContext());

        Mvcs.setActionContext(ac);
        
        ActionInvoker invoker = mapping.get(ac);
        if (null == invoker)
            return false;
        return invoker.invoke(ac);
    }

    public void depose() {
        loading.depose(config);
    }

}
