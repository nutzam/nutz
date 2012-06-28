package org.nutz.mvc.testapp.classes.action.ioc;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;

@IocBean(scope="request")
@InjectName
@At("/req")
public class ReqModule {

    @At
    public void me(){}
}
