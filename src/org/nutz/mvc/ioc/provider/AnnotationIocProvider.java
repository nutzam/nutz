package org.nutz.mvc.ioc.provider;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.mvc.IocProvider;

public class AnnotationIocProvider implements IocProvider {

	public Ioc create(ServletConfig config, String[] args) {
		return new NutIoc(new AnnotationIocLoader(args), new ScopeContext("app"), "app");
	}

}
