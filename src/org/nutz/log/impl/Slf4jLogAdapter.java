package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class Slf4jLogAdapter implements LogAdapter ,Plugin {

	public Log getLogger(String className) {
		return new Slf4JLog(className);
	}
	
	public boolean canWork() {
		try{
			Class.forName("org.slf4j.Logger",false,Thread.currentThread().getContextClassLoader());
			return true;
		}catch (Throwable e) {}
		return false;
	}

	static class Slf4JLog extends AbstractLog {

		private static final String FQCN = Slf4JLog.class.getName();

		protected Logger slf4jLogger;

		private LocationAwareLogger locationAwareLogger;

		public Slf4JLog(String name) {
			slf4jLogger = LoggerFactory.getLogger(name);
			if (slf4jLogger instanceof LocationAwareLogger) {
				locationAwareLogger = (LocationAwareLogger) slf4jLogger;
			}
			isDebugEnabled = slf4jLogger.isDebugEnabled();
			isInfoEnabled = slf4jLogger.isInfoEnabled();
			isWarnEnabled = slf4jLogger.isWarnEnabled();
			isErrorEnabled = slf4jLogger.isErrorEnabled();
			isFatalEnabled = slf4jLogger.isErrorEnabled();
		}

		@Override
		public void fatal(Object message, Throwable t) {
			if (isFatalEnabled())
				log(LocationAwareLogger.ERROR_INT, message, t);
		}

		@Override
		public void error(Object message, Throwable t) {
			if (isFatalEnabled())
				log(LocationAwareLogger.ERROR_INT, message, t);
		}

		@Override
		public void warn(Object message, Throwable t) {
			if (isFatalEnabled())
				log(LocationAwareLogger.WARN_INT, message, t);
		}

		@Override
		public void info(Object message, Throwable t) {
			if (isFatalEnabled())
				log(LocationAwareLogger.INFO_INT, message, t);
		}

		@Override
		public void debug(Object message, Throwable t) {
			if (isFatalEnabled())
				log(LocationAwareLogger.DEBUG_INT, message, t);
		}

		@Override
		public void trace(Object message, Throwable t) {
			if (isFatalEnabled())
				log(LocationAwareLogger.TRACE_INT, message, t);
		}

		@Override
		protected void log(int level, Object message, Throwable t) {
			String m = String.valueOf(message);
			if (locationAwareLogger != null) {
				locationAwareLogger.log(null, FQCN, level, m, null, t);
			} else {
				switch (level) {
				case LocationAwareLogger.TRACE_INT:
					slf4jLogger.trace(m, t);
					break;
				case LocationAwareLogger.DEBUG_INT:
					slf4jLogger.debug(m, t);
					break;
				case LocationAwareLogger.INFO_INT:
					slf4jLogger.info(m, t);
					break;
				case LocationAwareLogger.WARN_INT:
					slf4jLogger.warn(m, t);
					break;
				case LocationAwareLogger.ERROR_INT:
					slf4jLogger.error(m, t);
					break;
				case LEVEL_FATAL:
					slf4jLogger.error(m, t);
					break;
				}
			}
		}
	}
}
