package org.nutz.mvc;


import org.nutz.mvc.impl.ActionInvoker;

public interface Loading {

    String CONTEXT_NAME = "_NUTZ_LOADING_CONTEXT_";

    void init(NutConfig config);

    UrlMapping load(NutConfig config);

    void depose(NutConfig config);

    /**
     * 根据 ActionContext 获取一个ActionInvoker
     * 提交一个可以在运行过程中动态生成ActionContext的机会。
     * @param ac
     * @return
     */
    ActionInvoker fetch(ActionContext ac);
}
