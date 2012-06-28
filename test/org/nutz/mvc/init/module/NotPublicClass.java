package org.nutz.mvc.init.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
class NotPublicClass {

    @At("/here")
    @Ok("json")
    public String here(){
        return "asfdasdf";
    }
    
}
