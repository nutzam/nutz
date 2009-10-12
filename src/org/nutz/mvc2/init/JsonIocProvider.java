package org.nutz.mvc2.init;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.JsonLoader;
import org.nutz.mvc2.IocProvider;

public class JsonIocProvider implements IocProvider {

	public Ioc create(ServletConfig config, String[] args) {
		return new NutIoc(new JsonLoader(args));
	}

}
