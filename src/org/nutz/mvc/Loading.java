package org.nutz.mvc;


import org.nutz.mvc.impl.ActionInvoker;

/**
 * 根据NutConfig环境里面的配置信息对MVC进行初始化。构建出完成的URL与处理器间的映射关系
 */
public interface Loading {

    String CONTEXT_NAME = "_NUTZ_LOADING_CONTEXT_";

    /**
     * 加载路径映射
     * @param config
     * @return
     */
    UrlMapping load(NutConfig config);

    /**
     * 根据 ActionContext 获取一个ActionInvoker
     * 提交一个可以在运行过程中动态生成ActionContext的机会。
     * @param ac
     * @return
     */
    ActionInvoker fetch(ActionContext ac);

    void depose(NutConfig config);
}
