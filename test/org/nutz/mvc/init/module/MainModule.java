package org.nutz.mvc.init.module;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.init.JsonIocProvider;

@Modules(value={},packages={"org.nutz.mvc.init.module"})
@IocBy(type=JsonIocProvider.class,args={"org/nutz/mvc/init/module/base.js"})
public class MainModule {
}
