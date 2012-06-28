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
            return new NutIoc(new ComboIocLoader(args), new ScopeContext("app"), "app");
        }
        catch (ClassNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
