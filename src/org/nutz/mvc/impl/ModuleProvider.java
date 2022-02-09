package org.nutz.mvc.impl;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.Setup;

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

    Map<String, Map<String, Object>> getMessageSet();

    String getDefaultLocalizationKey();
}
