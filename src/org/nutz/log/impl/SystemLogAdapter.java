package org.nutz.log.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;

public class SystemLogAdapter implements LogAdapter, Plugin {

	public Log getLogger(String className) {
		return SystemLog.me();
	}

	public boolean canWork() {
		return true;
	}

	/**
	 * 默认的Log,输出到System.out和System.err
	 * 
	 * @author Young(sunonfire@gmail.com)
	 * @author Wendal(wendal1985@gmail.com)
	 */
	public static class SystemLog extends AbstractLog {

		private final static SystemLog me = new SystemLog();
		
		private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		private static boolean warned;

		static SystemLog me() {
			if (! warned) {
				me.warn("!!You are using default SystemLog! Don't use it in Production environment!!");
				warned = true;
			}
			return me;
		}

		private SystemLog() {
			isInfoEnabled = true;
			isDebugEnabled = true;
		}

		private void printOut(String level, Object message, Throwable t) {
			System.out.printf("%s %s [%s] %s\n",DATE_FORMAT.format(new Date()), level, Thread.currentThread().getName(),message);
			if (t != null)
				t.printStackTrace(System.out);
		}

		private void errorOut(String level, Object message, Throwable t) {
			System.err.printf("%s %s [%s] %s\n",DATE_FORMAT.format(new Date()), level, Thread.currentThread().getName(),message);
			if (t != null)
				t.printStackTrace(System.err);
		}

		@Override
		protected void log(int level, Object message, Throwable tx) {
			switch (level) {
			case LEVEL_ERROR:
				errorOut("ERROR",message, tx);
				break;
			case LEVEL_WARN:
				printOut("WARN",message, tx);
				break;
			case LEVEL_INFO:
				printOut("INFO",message, tx);
				break;
			case LEVEL_DEBUG:
				printOut("DEBUG",message, tx);
				break;
			case LEVEL_TRACE:
				printOut("TRACE",message, tx);
				break;
			}
		}

	}
}
