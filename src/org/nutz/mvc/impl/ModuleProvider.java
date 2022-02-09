package org.nutz.mvc.impl;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.SessionProvider;
import org.nutz.mvc.Setup;
import org.nutz.mvc.ViewMaker;

import java.util.List;
import java.util.Map;

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
}
