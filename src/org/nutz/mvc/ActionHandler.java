package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.impl.ActionInvoker;

public class ActionHandler {

    private Loading loading;

    private NutConfig config;

    public ActionHandler(NutConfig config) {
        this.config = config;
        this.loading = config.createLoading();
        loading.init(config);
    }

    public boolean handle(HttpServletRequest req, HttpServletResponse resp) {
        ActionContext ac = new ActionContext();
        ac.setRequest(req).setResponse(resp).setServletContext(config.getServletContext());

        Mvcs.setActionContext(ac);

        ActionInvoker invoker = loading.load(ac);
        if(null == invoker) {
            return false;
        }
        return invoker.invoke(ac);
    }

    public void depose() {
        loading.depose(config);
    }

}
