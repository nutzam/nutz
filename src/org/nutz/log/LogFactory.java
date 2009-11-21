package org.nutz.log;

import java.util.LinkedList;
import java.util.List;

import org.nutz.log.impl.JdkLoggerAdapter;
import org.nutz.log.impl.Log4jAdapter;
import org.nutz.log.impl.NullAdaptor;
import org.nutz.log.impl.SystemLog;

/**
 * 获取 Log 的静态工厂方法
 * 
 * @author Young(sunonfire@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LogFactory {

	private static Log systemLog = new SystemLog();

	private static LogAdapter workableAdapter = null;

	private static List<LogAdapter> adapters = new LinkedList<LogAdapter>();

	private static boolean showNullLogWarning = true;

	static {
		registerLogAdapter(new Log4jAdapter());
		registerLogAdapter(new JdkLoggerAdapter());
	}

	public static void turnOnNullLogWarning() {
		showNullLogWarning = true;
	}

	public static void turnOffNullLogWarning() {
		showNullLogWarning = false;
	}

	public static void registerLogAdapter(LogAdapter adapter) {
		adapters.add(adapter);
	}

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String className) {
		if (workableAdapter == null) {
			synchronized (LogFactory.class) {
				if (workableAdapter == null) {
					for (LogAdapter adapter : adapters) {
						if (adapter.canWork()) {
							workableAdapter = adapter;
							break;
						}
					}
					if (showNullLogWarning && workableAdapter == null)
						systemLog.fatal("failed to create logger from logAdapter: "
								+ workableAdapter.getClass().getName()
								+ ", nullLog will be used instead of it.");
					workableAdapter = new NullAdaptor();
				}
			}
		}
		try {
			return workableAdapter.getLogger(className);
		} catch (Exception e) {
			systemLog.fatal("failed to create logger from logAdapter: "
					+ workableAdapter.getClass().getName()
					+ ", nullLog will be used instead of it.", e);

			return NullAdaptor.log;
		}

	}
}
