package org.nutz.mvc.init.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.impl.UrlMappingImpl;

@IocBean
public class MyUrlMappingImpl extends UrlMappingImpl {

    public MyUrlMappingImpl() {
        System.out.println("I am Here");
    }
    
}
