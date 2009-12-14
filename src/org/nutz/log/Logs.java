package org.nutz.log;

import org.nutz.plugin.SimplePluginManager;

/**
 * 获取 Log 的静态工厂方法
 * 
 * @author Young(sunonfire@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class Logs {

	private static LogAdapter adapter;

	static {
		try {
			adapter = new SimplePluginManager<LogAdapter>("org.nutz.log.impl.Log4jLogAdapter",
					"org.nutz.log.impl.JdkLogAdapter", "org.nutz.log.impl.SystemLogAdapter").get();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String className) {
		return adapter.getLogger(className);
	}

}
