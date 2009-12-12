package org.nutz.log.impl;

import org.apache.commons.logging.LogFactory;
import org.nutz.log.Log;
import org.nutz.plugin.NutPluginConfig;

/**
 * Apache Commons Logging 1.1.x适配器
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public class JclLoggerAdapter extends AbstractLogAdapter {
	
	private static Log rootLog;

	public Log getLogger(String className) {
		return new JclLogger(className);
	}

	public Log getRootLogger() {
		if(rootLog == null)
			rootLog = new JclLogger();
		return rootLog;
	}

	public boolean canWork(NutPluginConfig config) {
		try{
			LogFactory.getFactory();
			return true;
		}catch (Throwable e) {
		}
		return false;
	}

	class JclLogger extends AbstractLog {

		private org.apache.commons.logging.Log log;

		public JclLogger() {
			log = LogFactory.getLog("Nutz");
			loadLevel();
		}

		public JclLogger(String className) {
			log = LogFactory.getLog(className);
			loadLevel();
		}

		private void loadLevel() {
			isFatalEnabled = log.isFatalEnabled();
			isErrorEnabled = log.isErrorEnabled();
			isWarnEnabled = log.isWarnEnabled();
			isInfoEnabled = log.isInfoEnabled();
			isDebugEnabled = log.isDebugEnabled();
			isTraceEnabled = log.isTraceEnabled();
		}

		public void debug(Object message, Throwable t) {
			if (isDebugEnabled()) {
				if (t == null)
					log.debug(message);
				else
					log.debug(message, t);
			}

		}

		public void error(Object message, Throwable t) {
			if (isErrorEnabled()) {
				if (t == null)
					log.error(message);
				else
					log.error(message, t);
			}
		}

		public void fatal(Object message, Throwable t) {
			if (isFatalEnabled()) {
				if (t == null)
					log.fatal(message);
				else
					log.fatal(message, t);
			}
		}

		public void info(Object message, Throwable t) {
			if (isInfoEnabled()) {
				if (t == null)
					log.info(message);
				else
					log.info(message, t);
			}
		}

		public void trace(Object message, Throwable t) {
			if (isTraceEnabled()) {
				if (t == null)
					log.trace(message);
				else
					log.trace(message, t);
			}
		}

		public void warn(Object message, Throwable t) {
			if (isWarnEnabled()) {
				if (t == null)
					log.warn(message);
				else
					log.warn(message, t);
			}
		}
	}

}
