package org.nutz.mvc;

import org.nutz.mvc.impl.ActionInvoker;

/**
 * 路径映射
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface UrlMapping {

    /**
     * 增加一个映射
     * 
     * @param maker
     *            处理器工厂
     * @param ai
     *            处理器配置
     */
    void add(ActionChainMaker maker, ActionInfo ai, NutConfig config);

    /**
     * 根据一个路径，获取一个动作链的调用者，并且，如果这个路径中包括统配符 '?' 或者 '*' <br>
     * 需要为上下文对象设置好路径参数
     * 
     * @param ac
     *            上下文对象
     * @return 动作链的调用者
     */
    ActionInvoker get(ActionContext ac);

}
