package org.nutz.mvc.init;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mvc.Setup;
import org.nutz.mvc.UrlMap;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.view.BuiltinViewMaker;

public class Launching {

	private ServletConfig config;
	private UrlMapImpl urls;
	private Ioc ioc;
	private Map<String, String> msgs;

	public Launching(ServletConfig config) {
		this.config = config;
	}

	public void launch(Class<?> klass) {
		try {
			Modules modules = klass.getAnnotation(Modules.class);
			IocBy ib = klass.getAnnotation(IocBy.class);
			SetupBy sb = klass.getAnnotation(SetupBy.class);
			Views vms = klass.getAnnotation(Views.class);
			Localization lc = klass.getAnnotation(Localization.class);

			// Load Ioc
			if (null != ib) {
				ioc = ib.provider().newInstance().create(config, ib.args());
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
			if (null != modules)
				for (Class<?> module : modules.value())
					urls.add(makers, module);
			config.getServletContext().setAttribute(UrlMap.class.getName(), urls);

			// Load Localization Message
			if (null != lc) {
				File dir = Files.findFile(lc.value());
				if (null != dir) {
					msgs = new HashMap<String, String>();
					File[] files = dir.listFiles(new FileFilter() {
						public boolean accept(File f) {
							if (f.isFile())
								if (f.getName().endsWith(".msg"))
									return true;
							return false;
						}
					});
					for (File f : files) {
						Properties p = new Properties();
						Reader reader = Streams.fileInr(f);
						try {
							p.load(reader);
						} catch (IOException e) {
							throw Lang.wrapThrow(e);
						} finally {
							try {
								reader.close();
							} catch (IOException e) {
								throw Lang.wrapThrow(e);
							}
						}
						for (Entry<?, ?> en : p.entrySet())
							msgs.put(en.getKey().toString(), en.getValue().toString());
					}
				}
			}

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

	public Map<String, String> getMsgs() {
		return msgs;
	}

}
