package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.plugin.NutPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Slf4j 1.5.x适配器
 * <p/><b>Slf4j 没有Fatal级别的Log, 默认转为使用Error级别来Log</b>
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public class Slf4jLoggerAdapter extends AbstractLogAdapter {
	
	private static Log rootLog;

	public Log getLogger(String className) {
		return new Slf4jLogger(className);
	}

	public Log getRootLogger() {
		if(rootLog == null)
			rootLog = new Slf4jLogger();
		return rootLog;
	}

	public boolean canWork(NutPluginConfig config) {
		try{
			LoggerFactory.getILoggerFactory();
			return true;
		}catch (Throwable e) {
		}
		return false;
	}

	class Slf4jLogger extends AbstractLog {

		private Logger log;

		public Slf4jLogger() {
			log = LoggerFactory.getLogger("Nutz");
			loadLevel();
		}

		public Slf4jLogger(String className) {
			log = LoggerFactory.getLogger(className);
			loadLevel();
		}

		private void loadLevel() {
			isFatalEnabled = log.isErrorEnabled();//
			isErrorEnabled = log.isErrorEnabled();
			isWarnEnabled = log.isWarnEnabled();
			isInfoEnabled = log.isInfoEnabled();
			isDebugEnabled = log.isDebugEnabled();
			isTraceEnabled = log.isTraceEnabled();
		}

		public void debug(Object message, Throwable t) {
			if (isDebugEnabled()) {
				if (t == null)
					log.debug(String.valueOf(message));
				else
					log.debug(String.valueOf(message), t);
			}

		}

		public void error(Object message, Throwable t) {
			if (isErrorEnabled()) {
				if (t == null)
					log.error(String.valueOf(message));
				else
					log.error(String.valueOf(message), t);
			}
		}

		public void fatal(Object message, Throwable t) {
			if (isFatalEnabled()) {
				if (t == null)
					log.error(String.valueOf(message));
				else
					log.error(String.valueOf(message), t);
			}
		}

		public void info(Object message, Throwable t) {
			if (isInfoEnabled()) {
				if (t == null)
					log.info(String.valueOf(message));
				else
					log.info(String.valueOf(message), t);
			}
		}

		public void trace(Object message, Throwable t) {
			if (isTraceEnabled()) {
				if (t == null)
					log.trace(String.valueOf(message));
				else
					log.trace(String.valueOf(message), t);
			}
		}

		public void warn(Object message, Throwable t) {
			if (isWarnEnabled()) {
				if (t == null)
					log.warn(String.valueOf(message));
				else
					log.warn(String.valueOf(message), t);
			}
		}
	}

}
