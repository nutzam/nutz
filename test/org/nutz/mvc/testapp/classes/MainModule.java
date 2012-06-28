package org.nutz.mvc.testapp.classes;

import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@Modules(scanPackage=true)
@Ok("json")
@Fail("json")
@IocBy(type=ComboIocProvider.class,
        args={"*org.nutz.ioc.loader.json.JsonLoader","org/nutz/mvc/testapp/classes/ioc",
              "*org.nutz.ioc.loader.annotation.AnnotationIocLoader","org.nutz.mvc.testapp.classes"})
@Localization("org/nutz/mvc/testapp/classes/message/")
public class MainModule {

}
