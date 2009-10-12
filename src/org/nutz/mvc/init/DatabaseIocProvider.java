package org.nutz.mvc.init;

import javax.servlet.ServletConfig;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.db.DatabaseLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.lang.Lang;
import org.nutz.mvc.DaoProvider;
import org.nutz.mvc.IocProvider;

public class DatabaseIocProvider implements IocProvider {

	public Ioc create(ServletConfig config, String[] args) {
		if (args.length == 0)
			throw Lang.makeThrow("Need an class name of DaoProvider as first argument");
		try {
			Class<?> providerClass = Class.forName(args[0]);
			DaoProvider provider = (DaoProvider) providerClass.newInstance();
			Dao dao = provider.create(config);
			return new NutIoc(new DatabaseLoader(dao));
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		} catch (InstantiationException e) {
			throw Lang.wrapThrow(e);
		} catch (IllegalAccessException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
