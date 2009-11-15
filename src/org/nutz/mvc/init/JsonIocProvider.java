package org.nutz.mvc.init;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.mvc.IocProvider;

public class JsonIocProvider implements IocProvider {

	public Ioc create(ServletConfig config, String[] args) {
		return new NutIoc(new JsonLoader(args), new ScopeContext("app"), "app");
	}

}
