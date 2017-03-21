package org.nutz.ioc.meta.issue1232;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name="a")
public class Issue1232ClassA {

    // 正确的注入是 refer:b, 这里特意注入错误的对象
    @Inject("refer:c")
    public Issue1232ClassB b;
}
