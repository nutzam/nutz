package org.nutz.ioc.aop.config.impl.simple;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class BeAop {

    @Def
    @Abc
    public void hi() {}
}
