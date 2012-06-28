package org.nutz.mvc.testapp.classes.action.ioc;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;

@IocBean(scope="session")
@InjectName
@At("/session")
public class SessionScopeModule {

    @At
    public void me(){}
}
