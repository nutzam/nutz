package org.nutz.ioc.meta.issue348;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class DogMaster {

    @Inject
    public Dog dog;
}
