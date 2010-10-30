package org.nutz.mvc.init;

import java.io.File;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Setup;
import org.nutz.mvc.annotation.LoadingBy;

public abstract class Inits {

	private static final Log log = Logs.getLog(Inits.class);
	
	/**
	 * 根据一个配置获取加载后的信息
	 * 
	 * @param config
	 *            配置信息
	 * @param ignoreNull
	 *            如果配置没有 mainModule 是返回 null 还是抛错。true 为返回 null
	 * @return 加载后的 Loading 对象
	 * @throws InitException
	 */
	public static Loading init(NutConfig config, boolean ignoreNull) throws InitException {
		try {
			if (log.isInfoEnabled()) {
				log.infof("Nutz.Mvc[%s] is initializing ...", config.getAppName());
			}
			printContainerInfo();
			Stopwatch sw = Stopwatch.begin();
			// Nutz.Mvc need a class name as the default Module
			// it will load some Annotation from it.
			Class<?> mainModule = config.getMainModule();
			if (null == mainModule) {
				if (ignoreNull)
					return null;
				throw new InitException("You need declare modules parameter in your context configuration file!");
			}

			// servlet support you setup your loading class, it must implement
			// "org.nutz.mvc.Loading"
			// And it must has one constructor, with one param type as
			// ServletConfig
			Class<? extends Loading> loadingType;
			LoadingBy lb = mainModule.getAnnotation(LoadingBy.class);
			if (null != lb)
				loadingType = lb.value();
			else
				loadingType = DefaultLoading.class;
			if (log.isDebugEnabled())
				log.debug("Loading by " + loadingType);
			// Here, we load all Nutz.Mvc configuration
			Loading ing = Mirror.me(loadingType).born();
			ing.load(config, mainModule);
			// Done, print info
			sw.stop();
			if (log.isInfoEnabled())
				log.infof("Nutz.Mvc[%s] is up in %sms", config.getAppName(), sw.getDuration());

			return ing;
		}
		catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error("Error happend during start serivce!", e);
			if (e instanceof InitException)
				throw (InitException) e;
			throw new InitException(Lang.unwrapThrow(e));
		}
	}

	public static void destroy(NutConfig config) throws InitException {
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
			throw new InitException(e);
		}
		// If the application has Ioc, depose it
		Ioc ioc = config.getIoc();
		if (null != ioc)
			ioc.depose();

		// Done, print info
		sw.stop();
		if (log.isInfoEnabled())
			log.infof("Nutz.Mvc[%s] is down in %sms", config.getAppName(), sw.getDuration());
	}

	private static void printContainerInfo() {
		if (log.isDebugEnabled()) {
			log.debug("Web Container Information:");
			log.debugf(" - Default Charset : %s", Encoding.defaultEncoding());
			log.debugf(" - Current . path  : %s", new File(".").getAbsolutePath());
			log.debugf(" - Java Version    : %s", System.getProperties().get("java.version"));
			log.debugf(" - File separator  : %s", System.getProperties().get("file.separator"));
			log.debugf(" - Timezone        : %s", System.getProperties().get("user.timezone"));
		}
	}

}
