package org.nutz.mvc.ioc.provider;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.xml.XmlIocLoader;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;

public class XmlIocProvider implements IocProvider {

    public Ioc create(NutConfig config, String[] args) {
        return new NutIoc(new XmlIocLoader(args), new ScopeContext("app"), "app");
    }

}
