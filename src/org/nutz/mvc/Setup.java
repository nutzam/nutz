package org.nutz.mvc;

/**
 * 整个应用启动以及关闭的时候需要做的额外逻辑
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Setup {
    
    String IOCNAME = "$setup";

    /**
     * 启动时，额外逻辑
     * 
     * @param nc 配置对象,包含Ioc等你需要的一切资源
     */
    void init(NutConfig nc);

    /**
     * 关闭时，额外逻辑
     * 
     * @param nc 配置对象,包含Ioc等你需要的一切资源
     */
    void destroy(NutConfig nc);

}
