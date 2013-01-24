package org.nutz.ioc.meta.issue348;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class Dog {

    public Dog() {
        // 总是抛出异常,这个bean就总无法被创建
        throw new RuntimeException();
    }
}
