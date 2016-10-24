package net.wendal.nutzdemo;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;

@IocBy(args = {"*js", "ioc/", "*anno", "net.wendal.nutzdemo", "*tx", "*async"})
@Modules(scanPackage = true)
@SetupBy(MainSetup.class)
public class MainModule {}
