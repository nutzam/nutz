package org.nutz.mvc.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.Nutz;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Loading;
import org.nutz.mvc.LoadingException;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.view.DefaultViewMaker;

public class NutLoading implements Loading {

	private static final Log log = Logs.get();

	public UrlMapping load(NutConfig config) {
		if (log.isInfoEnabled()) {
			log.infof("Nutz Version : %s ", Nutz.version());
			log.infof("Nutz.Mvc[%s] is initializing ...", config.getAppName());
		}
		if (log.isDebugEnabled()) {
			log.debug("Web Container Information:");
			log.debugf(" - Default Charset : %s", Encoding.defaultEncoding());
			log.debugf(" - Current . path  : %s", new File(".").getAbsolutePath());
			log.debugf(" - Java Version    : %s", System.getProperties().get("java.version"));
			log.debugf(" - File separator  : %s", System.getProperties().get("file.separator"));
			log.debugf(" - Timezone        : %s", System.getProperties().get("user.timezone"));
		}
		/*
		 * 准备返回值
		 */
		UrlMapping mapping;

		/*
		 * 准备计时
		 */
		Stopwatch sw = Stopwatch.begin();

		try {

			/*
			 * 检查主模块，调用本函数前，已经确保过有声明 MainModule 了
			 */
			Class<?> mainModule = config.getMainModule();

			/*
			 * 创建上下文
			 */
			createContext(config);

			/*
			 * 检查 Ioc 容器并创建和保存它
			 */
			createIoc(config, mainModule);

			/*
			 * 组装UrlMapping
			 */
			mapping = evalUrlMapping(config, mainModule);

			/*
			 * 分析本地化字符串
			 */
			evalLocalization(config, mainModule);

			/*
			 * 执行用户自定义 Setup
			 */
			evalSetup(config, mainModule);
		}
		catch (Exception e) {
			if (log.isErrorEnabled())
				log.error("Error happend during start serivce!", e);
			throw Lang.wrapThrow(e, LoadingException.class);
		}

		// ~ Done ^_^
		sw.stop();
		if (log.isInfoEnabled())
			log.infof("Nutz.Mvc[%s] is up in %sms", config.getAppName(), sw.getDuration());

		return mapping;

	}
	

	private UrlMapping evalUrlMapping(NutConfig config, Class<?> mainModule) throws Exception{
		/*
		 * @ TODO 个人建议可以将这个方法所涉及的内容转换到Loadings类或相应的组装类中,
		 * 以便将本类加以隔离,使本的职责仅限于MVC整体的初使化,而不再负责UrlMapping的加载
		 */
		
		/*
		 * 准备 UrlMapping
		 */
		UrlMapping mapping = createUrlMapping(config);
		if (log.isInfoEnabled())
			log.infof("Build URL mapping by %s ...", mapping.getClass().getName());

		/*
		 * 创建视图工厂
		 */
		ViewMaker[] makers = createViewMakers(mainModule);

		/*
		 * 创建动作链工厂
		 */
		ActionChainMaker maker = createChainMaker(config, mainModule);

		/*
		 * 创建主模块的配置信息
		 */
		ActionInfo mainInfo = Loadings.createInfo(mainModule);

		/*
		 * 准备要加载的模块列表
		 */
		//TODO 为什么用Set呢? 用List不是更快吗?
		Set<Class<?>> modules = Loadings.scanModules(mainModule);

		/*
		 * 分析所有的子模块
		 */
		for (Class<?> module : modules) {
			ActionInfo moduleInfo = Loadings.createInfo(module).mergeWith(mainInfo);
			for (Method method : module.getMethods()) {
				/*
				 * public 并且声明了 @At 的函数，才是入口函数
				 */
				if (!Modifier.isPublic(method.getModifiers())
					|| !method.isAnnotationPresent(At.class))
					continue;
				// 增加到映射中
				ActionInfo info = Loadings.createInfo(method).mergeWith(moduleInfo);
				info.setViewMakers(makers);
				mapping.add(maker, info, config);
			}
			
			//记录pathMap
			if(null != moduleInfo.getPathMap()){
				for(Entry<String, String> en : moduleInfo.getPathMap().entrySet()){
					config.getAtMap().add(en.getKey(), en.getValue());
				}
			}
		}
		return mapping;
	}

	private static void createContext(NutConfig config) {
		// 构建一个上下文对象，方便子类获取更多的环境信息
		// 同时，所有 Filter 和 Adaptor 都可以用 ${app.root} 来填充自己
		Context context = Lang.context();
		context.set("app.root", config.getAppRoot());

		if (log.isDebugEnabled()) {
			log.debugf(">> app.root = %s", config.getAppRoot());
		}

		// 载入环境变量
		for (Entry<String, String> entry : System.getenv().entrySet())
			context.set("env." + entry.getKey(), entry.getValue());
		// 载入系统变量
		for (Entry<Object, Object> entry : System.getProperties().entrySet())
			context.set("sys." + entry.getKey(), entry.getValue());

		if (log.isTraceEnabled()) {
			log.tracef(">>\nCONTEXT %s", Json.toJson(context, JsonFormat.nice()));
		}
		config.getServletContext().setAttribute(Loading.CONTEXT_NAME, context);
	}
	

	private UrlMapping createUrlMapping(NutConfig config) throws Exception {
		UrlMappingBy umb = config.getMainModule().getAnnotation(UrlMappingBy.class);
		if (umb != null)
			return Loadings.evalObj(config, umb.value(), umb.args());
		return new UrlMappingImpl();
	}

	private ActionChainMaker createChainMaker(NutConfig config, Class<?> mainModule) {
		ChainBy ann = mainModule.getAnnotation(ChainBy.class);
		ActionChainMaker maker = null == ann ? new NutActionChainMaker(new String[]{})
											: Loadings.evalObj(config, ann.type(), ann.args());
		if (log.isDebugEnabled())
			log.debugf("@ChainBy(%s)", maker.getClass().getName());
		return maker;
	}

	private void evalSetup(NutConfig config, Class<?> mainModule) throws Exception {
		SetupBy sb = mainModule.getAnnotation(SetupBy.class);
		if (null != sb) {
			if (log.isInfoEnabled())
				log.info("Setup application...");
			Setup setup = Loadings.evalObj(config, sb.value(), sb.args());
			config.setAttributeIgnoreNull(Setup.class.getName(), setup);
			setup.init(config);
		}
	}

	private void evalLocalization(NutConfig config, Class<?> mainModule) {
		Localization lc = mainModule.getAnnotation(Localization.class);
		if (null != lc) {
			if (log.isDebugEnabled())
				log.debugf("Localization message: '%s'", lc.value());

			Map<String, Map<String, Object>> msgss = Mirror.me(lc.type()).born().load(lc.value());
			config.setAttributeIgnoreNull(Localization.class.getName(), msgss);
		} else if (log.isDebugEnabled()) {
			log.debug("!!!Can not find localization message resource");
		}
	}


	private ViewMaker[] createViewMakers(Class<?> mainModule) throws Exception {
		Views vms = mainModule.getAnnotation(Views.class);
		ViewMaker[] makers;
		int i = 0;
		if (null != vms) {
			makers = new ViewMaker[vms.value().length + 1];
			for (; i < vms.value().length; i++)
				makers[i] = Mirror.me(vms.value()[i]).born();

		} else {
			makers = new ViewMaker[1];
		}
		makers[i] = new DefaultViewMaker();// 优先使用用户自定义

		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append(makers[0].getClass().getSimpleName());
			for (i = 1; i < makers.length; i++)
				sb.append(',').append(makers[i].getClass().getSimpleName());
			log.debugf("@Views(%s)", sb);
		}

		return makers;
	}

	private void createIoc(NutConfig config, Class<?> mainModule) throws Exception {
		IocBy ib = mainModule.getAnnotation(IocBy.class);
		if (null != ib) {
			if (log.isDebugEnabled())
				log.debugf("@IocBy(%s)", ib.type().getName());

			Ioc ioc = Mirror.me(ib.type()).born().create(config, ib.args());
			// 如果是 Ioc2 的实现，增加新的 ValueMaker
			if (ioc instanceof Ioc2) {
				((Ioc2) ioc).addValueProxyMaker(new ServletValueProxyMaker(config.getServletContext()));
			}
			// 保存 Ioc 对象
			config.setAttributeIgnoreNull(Ioc.class.getName(), ioc);

		} else if (log.isInfoEnabled())
			log.info("!!!Your application without @IocBy supporting");
	}


	public void depose(NutConfig config) {
		if (log.isInfoEnabled())
			log.infof("Nutz.Mvc[%s] is deposing ...", config.getAppName());
		Stopwatch sw = Stopwatch.begin();

		// Firstly, upload the user customized desctroy
		try {
			Setup setup = config.getAttributeAs(Setup.class, Setup.class.getName());
			if (null != setup)
				setup.destroy(config);
		}
		catch (Exception e) {
			throw new LoadingException(e);
		}
		finally {
			// If the application has Ioc, depose it
			Ioc ioc = config.getIoc();
			if (null != ioc)
				ioc.depose();
		}

		// Done, print info
		sw.stop();
		if (log.isInfoEnabled())
			log.infof("Nutz.Mvc[%s] is down in %sms", config.getAppName(), sw.getDuration());
	}

}
