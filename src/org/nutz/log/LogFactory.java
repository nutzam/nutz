package org.nutz.log;

import java.util.LinkedList;
import java.util.List;

import org.nutz.log.impl.JdkLoggerAdapter;
import org.nutz.log.impl.Log4jAdapter;
import org.nutz.log.impl.NullLog;
import org.nutz.log.impl.SystemLog;

/**
 * 
 * @author Young(sunonfire@gmail.com)
 *
 */
public class LogFactory {

	static Log nullLog = new NullLog();

	static Log systemLog = new SystemLog();
	
	static LogAdapter workableAdapter = null;

	static List<LogAdapter> adapters = null;

	static boolean notFound = false;

	static {
		registerLogAdapter(new Log4jAdapter());
		registerLogAdapter(new JdkLoggerAdapter());
	}

	public static void registerLogAdapter(LogAdapter adapter) {
		if (adapters == null)
			adapters = new LinkedList<LogAdapter>();

		adapters.add(adapter);
	}

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String className) {
		if (workableAdapter != null) {
			try {
				return workableAdapter.getLogger(className);
			} catch (Exception e) {
				systemLog.fatal("failed to create logger from logAdapter: " + 
						workableAdapter.getClass().getName() +
						", nullLog will be used instead of it.", e);
				
				return nullLog;
			}
		}

		if (notFound || adapters == null) {
			return nullLog;
		}

		for (LogAdapter adapter : adapters) {
			if (adapter.canWork()) {
				workableAdapter = adapter;
				
				try {
					return workableAdapter.getLogger(className);
				} catch (Exception e) {
					
					systemLog.fatal("failed to create logger from logAdapter: " + 
							workableAdapter.getClass().getName() +
							", nullLog will be used instead of it.", e);

					return nullLog;
				}
			}
		}

		// to avoid searching again...
		notFound = true;

		return nullLog;
	}
}
