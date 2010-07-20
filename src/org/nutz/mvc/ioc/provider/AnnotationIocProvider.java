package org.nutz.mvc.ioc.provider;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc.init.config.AbstractNutConfig;
import org.nutz.resource.impl.WebResourceScan;

public class AnnotationIocProvider implements IocProvider {

	public Ioc create(NutConfig config, String[] args) {
		return new NutIoc(	new AnnotationIocLoader(new WebResourceScan(((AbstractNutConfig) config).getServletContext())),
							new ScopeContext("app"),
							"app");
	}

}
