package org.nutz.mvc.init.module;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@Modules(scanPackage = true)
@IocBy(type=ComboIocProvider.class,args={
    "*org.nutz.ioc.loader.json.JsonLoader","org/nutz/mvc/init/module/base.js",
    "*org.nutz.ioc.loader.annotation.AnnotationIocLoader","org.nutz.mvc.init.module"})
@UrlMappingBy(args={"ioc:myUrlMappingImpl"})
public class MainModule {}
