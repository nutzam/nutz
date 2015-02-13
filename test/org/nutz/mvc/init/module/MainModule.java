package org.nutz.mvc.init.module;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@Modules(scanPackage = true)
@IocBy(type=ComboIocProvider.class,args={
    "*json","org/nutz/mvc/init/module/base.js",
    "*anno","org.nutz.mvc.init.module",
    "*tx"})
@UrlMappingBy(args={"ioc:myUrlMappingImpl"})
public class MainModule {}
