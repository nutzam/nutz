package org.nutz.mvc.init;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Setup;
import org.nutz.mvc.UrlMap;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.view.DefaultViewMaker;
import org.nutz.resource.Scans;

public class DefaultLoading implements Loading {

	private static final Log log = Logs.getLog(DefaultLoading.class);

	private UrlMap urls;
	protected Ioc ioc;
	private Map<String, Map<String, String>> msgss;
	private Context context;
	protected Class<?> mainModule;

	public void load(NutConfig config, Class<?> mainModule) {
		try {
			if (log.isDebugEnabled())
				log.debug("Init config ...");
			context = new Context();
			context.set("app.root", config.getAppRoot());
			if (log.isDebugEnabled())
				log.debugf(">>\nCONTEXT %s", Json.toJson(context, JsonFormat.nice()));

			if (log.isDebugEnabled())
				log.debug("Loading configuration...");
			this.mainModule = mainModule;
			loadIoc(config);
			loadSubModules(config);
			loadLocalization(config);
			setupServer(config);
			saveResult2Context(config);
		}
		catch (Throwable e) {
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

	protected void loadIoc(NutConfig config) throws Throwable {
		IocBy ib = mainModule.getAnnotation(IocBy.class);
		if (null != ib) {
			if (log.isDebugEnabled())
				log.debugf("Create Ioc by '%s'", ib.type().getName());

			ioc = ib.type().newInstance().create(config, ib.args());
			config.setAttributeIgnoreNull(Ioc.class.getName(), ioc);
		} else if (log.isDebugEnabled())
			log.debug("!!!Your application without @Ioc supporting");
	}

	protected void loadSubModules(NutConfig config) throws Throwable {
		Views vms = mainModule.getAnnotation(Views.class);

		// Prepare view makers
		List<ViewMaker> makers = new ArrayList<ViewMaker>();
		if (null != vms)
			for (Class<? extends ViewMaker> type : vms.value())
				makers.add(type.newInstance());
		makers.add(new DefaultViewMaker());// 优先使用用户自定义

		// Load modules
		if (log.isDebugEnabled())
			log.debugf("MainModule: <%s>", mainModule.getName());

		urls = makeUrlMap(ioc, context, mainModule);
		Set<Class<?>> moduleSet = new HashSet<Class<?>>();

		// Add default module
		moduleSet.add(mainModule);

		// Then try to load sub-modules
		Modules modules = mainModule.getAnnotation(Modules.class);
		Class<?>[] moduleRefers;
		if (null == modules || null == modules.value() || modules.value().length == 0)
			moduleRefers = new Class<?>[]{mainModule};
		else
			moduleRefers = modules.value();

		// 扫描所有的
		boolean isNeedScanSubPackages = null == modules ? false : modules.scanPackage();
		for (Class<?> module : moduleRefers) {
			// 扫描这个类同包，以及所有子包的类
			if (isNeedScanSubPackages) {
				if (log.isDebugEnabled())
					log.debugf(" > scan '%s'", module.getPackage().getName());
				List<Class<?>> subs = Scans.inLocal(module);
				for (Class<?> sub : subs) {
					if (isModule(sub)) {
						if (log.isDebugEnabled())
							log.debugf("   >> add '%s'", sub.getName());
						moduleSet.add(sub);
					} else if (log.isTraceEnabled()) {
						log.tracef("   >> ignore '%s'", sub.getName());
					}
				}
			}
			// 仅仅加载自己
			else {
				if (isModule(module)) {
					if (log.isDebugEnabled())
						log.debugf(" > add '%s'", module.getName());
					moduleSet.add(module);
				} else if (log.isTraceEnabled()) {
					log.tracef(" > ignore '%s'", module.getName());
				}
			}
		}

		for (Class<?> module : moduleSet) {
			if (log.isDebugEnabled())
				log.debugf("Module: <%s>", module.getName());

			urls.add(makers, module);
		}
		config.setAttributeIgnoreNull(UrlMap.class.getName(), urls);
	}

	protected void loadLocalization(NutConfig config) throws Throwable {
		Localization lc = mainModule.getAnnotation(Localization.class);
		if (null != lc) {
			if (log.isDebugEnabled())
				log.debugf("Localization message: '%s'", lc.value());

			msgss = Mirror.me(lc.type()).born().load(config.scan(), lc.value());
		} else if (log.isDebugEnabled()) {
			log.debug("!!!Can not find localization message resource");
		}
	}

	protected void setupServer(NutConfig config) throws Throwable {
		SetupBy sb = mainModule.getAnnotation(SetupBy.class);

		if (null != sb) {
			if (log.isInfoEnabled())
				log.info("Setup application...");

			Setup setup = sb.value().newInstance();
			config.setAttributeIgnoreNull(Setup.class.getName(), setup);
			setup.init(config);
		}
	}

	protected void saveResult2Context(NutConfig config) {
		config.setAttributeIgnoreNull(UrlMap.class.getName(), getUrls());
		config.setAttributeIgnoreNull(Ioc.class.getName(), getIoc());
		config.setAttributeIgnoreNull(Localization.class.getName(), getMessageMap());
	}

	protected UrlMap makeUrlMap(Ioc ioc, Context context, Class<?> mainModule) {
		return new UrlMapImpl(ioc, context, mainModule);
	}

	private static boolean isModule(Class<?> classZ) {
		for (Method method : classZ.getMethods())
			if (Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(At.class))
				return true;
		return false;
	}
}
