package controllers;


import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.impl.NutLoading2;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@Modules(scanPackage=true)
@LoadingBy(NutLoading2.class)
@IocBy(type= ComboIocProvider.class,args={"*org.nutz.ioc.loader.json.JsonLoader","dao.js","*org.nutz.ioc.loader.annotation.AnnotationIocLoader2","services","controllers"})
public class MainModule {
	
	public static int max = 10;
	
}