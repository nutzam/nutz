package org.nutz.mvc.ioc.provider;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;

public class AnnotationIocProvider implements IocProvider {

    public Ioc create(NutConfig config, String[] args) {
    	if (args == null || args.length == 0)
    		args = new String[]{config.getMainModule().getPackage().getName()};
        return new NutIoc(new AnnotationIocLoader(args), new ScopeContext("app"), "app");
    }

}
