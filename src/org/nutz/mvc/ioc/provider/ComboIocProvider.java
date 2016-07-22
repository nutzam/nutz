package org.nutz.mvc.ioc.provider;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;

public class ComboIocProvider implements IocProvider {

    public Ioc create(NutConfig config, String[] args) {
        try {
            //TODO 扩展语法
            for (int i = 0; i < args.length; i++) {
                if (args[i].contains("${main}"))
                    args[i] = args[i].replace("${main}", config.getMainModule().getPackage().getName());
            }
            return new NutIoc(new ComboIocLoader(args), new ScopeContext("app"), "app");
        }
        catch (ClassNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
