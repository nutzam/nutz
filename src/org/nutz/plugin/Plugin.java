package org.nutz.plugin;

/**
 * 插件 -- 一个通用的扩展点
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Plugin {

    /**
     * @return 当前插件是否能正常工作
     */
    boolean canWork();

}
