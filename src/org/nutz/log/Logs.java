package org.nutz.log;

import org.nutz.plugin.SimplePluginManager;

/**
 * 获取 Log 的静态工厂方法
 * 
 * @author Young(sunonfire@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public final class Logs {

	private static LogAdapter adapter;

	static {
		init();
	}

	/**
	 * Get a Log by Class
	 * 
	 * @param clazz
	 *            your class
	 * @return Log
	 * @throws NullPointerException
	 *             when clazz is null
	 */
	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	/**
	 * Get a Log by name
	 * 
	 * @param className
	 *            the name of Log
	 * @return Log
	 * @throws NullPointerException
	 *             when className is null, maybe it will case NPE
	 */
	public static Log getLog(String className) {
		return adapter.getLogger(className);
	}

	/**
	 * 初始化NutLog,检查全部Log的可用性,选择可用的Log适配器
	 * <p/>
	 * <b>加载本类时,该方法已经在静态构造函数中调用,用户无需主动调用.</b>
	 * <p/>
	 * <b>除非迫不得已,请不要调用本方法<b/>
	 */
	public static void init() {
		try {
			adapter = new SimplePluginManager<LogAdapter>(
					"org.nutz.log.impl.Log4jLogAdapter",
					"org.nutz.log.impl.JdkLogAdapter",
					"org.nutz.log.impl.Slf4jLogAdapter",
					"org.nutz.log.impl.SystemLogAdapter").get();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
