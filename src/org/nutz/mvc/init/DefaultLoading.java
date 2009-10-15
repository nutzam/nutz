package org.nutz.mvc.init;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Setup;
import org.nutz.mvc.UrlMap;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.view.BuiltinViewMaker;

public class DefaultLoading implements Loading {

	private ServletConfig config;
	private UrlMapImpl urls;
	private Ioc ioc;
	private Map<String, Map<String, String>> msgss;

	public DefaultLoading(ServletConfig config) {
		this.config = config;
	}

	public void load(Class<?> klass) {
		try {
			Modules modules = klass.getAnnotation(Modules.class);
			IocBy ib = klass.getAnnotation(IocBy.class);
			SetupBy sb = klass.getAnnotation(SetupBy.class);
			Views vms = klass.getAnnotation(Views.class);
			Localization lc = klass.getAnnotation(Localization.class);

			// Load Ioc
			if (null != ib) {
				ioc = ib.type().newInstance().create(config, ib.args());
				config.getServletContext().setAttribute(Ioc.class.getName(), ioc);
			}
			// Prepare view makers
			ArrayList<ViewMaker> makers = new ArrayList<ViewMaker>();
			makers.add(new BuiltinViewMaker());
			if (null != vms)
				for (Class<? extends ViewMaker> type : vms.value())
					makers.add(type.newInstance());

			// Load modules
			urls = new UrlMapImpl(ioc);
			urls.OK = klass.getAnnotation(Ok.class);
			urls.FAIL = klass.getAnnotation(Fail.class);
			urls.AB = klass.getAnnotation(AdaptBy.class);
			urls.FLTS = klass.getAnnotation(Filters.class);
			if (null != modules)
				for (Class<?> module : modules.value())
					urls.add(makers, module);
			config.getServletContext().setAttribute(UrlMap.class.getName(), urls);

			// Load localization
			if (null != lc)
				msgss = Mirror.me(lc.type()).born((Object[]) lc.args()).load();

			// Setup server
			if (null != sb) {
				Setup setup = sb.value().newInstance();
				config.getServletContext().setAttribute(Setup.class.getName(), setup);
				setup.init(config);
			}
		} catch (InstantiationException e) {
			throw Lang.wrapThrow(e);
		} catch (IllegalAccessException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public UrlMap getUrls() {
		return urls;
	}

	public Ioc getIoc() {
		return ioc;
	}

	public Map<String, Map<String, String>> getMessageMap() {
		return msgss;
	}

}
