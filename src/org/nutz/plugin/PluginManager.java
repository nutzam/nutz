package org.nutz.plugin;

import java.util.List;

public interface PluginManager<T> {

    /**
     * 获取可用的插件
     * @return 第一个可用的插件
     * @throws NoPluginCanWorkException 如果没有任何插件是可用的
     */
    T get() throws NoPluginCanWorkException;

    /**
     * 获取全部可用的插件
     * @return 全部可用的插件
     */
    List<T> gets();
}
