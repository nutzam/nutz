package org.nutz.mvc.impl;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.Setup;

import java.util.List;

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
}
