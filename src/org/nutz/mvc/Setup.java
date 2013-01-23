package org.nutz.mvc;

/**
 * 整个应用启动以及关闭的时候需要做的额外逻辑
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Setup {

    /**
     * 启动时，额外逻辑
     * 
     * @param config
     */
    void init(NutConfig nc);

    /**
     * 关闭时，额外逻辑
     * 
     * @param config
     */
    void destroy(NutConfig nc);

}
