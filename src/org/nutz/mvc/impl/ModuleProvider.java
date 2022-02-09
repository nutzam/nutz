package org.nutz.mvc.impl;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.*;

import java.util.List;
import java.util.Map;

/**
 * 模块提供者
 * 当前实现了基于注解的加载，理论上可以实现基于json、xml等类型的实现
 * @author juqkai(juqkai@gmail.com)
 */
public interface ModuleProvider {
    /**
     * 创建IOC
     * @return
     */
    Ioc createIoc();

    /**
     * 创建setup
     * @return
     */
    List<Setup> getSetup();

    List<ViewMaker> getViewMakers();

    ActionChainMaker getChainMaker();


    Map<String, Map<String, Object>> getMessageSet();

    String getDefaultLocalizationKey();

    SessionProvider getSessionProvider();

    EntryDeterminer getDeterminer();

    UrlMapping getUrlMapping();

    /**
     * 加载当前模块范围内所有的ActionInfo
     * @return
     */
    public List<ActionInfo> loadActionInfos();
}
