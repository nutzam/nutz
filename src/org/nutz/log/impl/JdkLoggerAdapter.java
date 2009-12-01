package org.nutz.log.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.nutz.log.Log;

/**
 * JDK Logger的适配器。
 * <p>
 * 如果系统属性java.util.logging.config.class或java.util.logging.config.
 * file属性非空我们就认为用户想用JDK Logger.
 * <p>
 * 
 * JDK Logger的日志级别和Log接口不同，对应关系请参考常量的定义。
 * 
 * 
 * @author Young(sunonfire@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class JdkLoggerAdapter extends AbstractLogAdapter implements Log {

	public static final Level TRACE_LEVEL = Level.FINEST;
	public static final Level DEBUG_LEVEL = Level.FINE;
	public static final Level INFO_LEVEL = Level.INFO;
	public static final Level WARN_LEVEL = Level.WARNING;
	public static final Level FATAL_LEVEL = Level.SEVERE;
	public static final Level ERROR_LEVEL = Level.SEVERE;

	private Logger jdkLogger = null;

	public JdkLoggerAdapter(String className) {

		jdkLogger = Logger.getLogger(className);

		isFatalEnabled = jdkLogger.isLoggable(FATAL_LEVEL);

		isErrorEnabled = jdkLogger.isLoggable(ERROR_LEVEL);

		isWarnEnabled = jdkLogger.isLoggable(WARN_LEVEL);

		isInfoEnabled = jdkLogger.isLoggable(INFO_LEVEL);

		isDebugEnabled = jdkLogger.isLoggable(DEBUG_LEVEL);

		isTraceEnabled = jdkLogger.isLoggable(TRACE_LEVEL);
	}

	public JdkLoggerAdapter() {
	}

	public void fatal(Object message) {
		if (isFatalEnabled)
			log(FATAL_LEVEL, message);
	}

	public void fatal(Object message, Throwable t) {
		if (isFatalEnabled)
			log(FATAL_LEVEL, message, t);
	}

	public void error(Object message) {
		if (isErrorEnabled)
			log(ERROR_LEVEL, message);
	}

	public void error(Object message, Throwable t) {
		if (isErrorEnabled)
			log(ERROR_LEVEL, message, t);
	}

	public void warn(Object message) {
		if (isWarnEnabled)
			log(WARN_LEVEL, message);
	}

	public void warn(Object message, Throwable t) {
		if (isWarnEnabled)
			log(WARN_LEVEL, message, t);
	}

	public void info(Object message) {
		if (isInfoEnabled)
			log(INFO_LEVEL, message);
	}

	public void info(Object message, Throwable t) {
		if (isInfoEnabled)
			log(INFO_LEVEL, message, t);
	}

	public void debug(Object message) {
		if (isDebugEnabled)
			log(DEBUG_LEVEL, message);
	}

	public void debug(Object message, Throwable t) {
		if (isDebugEnabled)
			log(DEBUG_LEVEL, message, t);
	}

	public void trace(Object message) {
		if (isTraceEnabled)
			log(TRACE_LEVEL, message);
	}

	public void trace(Object message, Throwable t) {
		if (isTraceEnabled)
			log(TRACE_LEVEL, message, t);
	}

	public boolean canWork() {
		return System.getProperty("java.util.logging.config.class") != null
				|| System.getProperty("java.util.logging.config.file") != null;
	}

	public Log getLogger(String className) throws Exception {
		return new JdkLoggerAdapter(className);
	}

	/**
	 * for testing purpose only
	 */
	@Deprecated
	public Logger getJdkLogger() {
		return jdkLogger;
	}

	private void log(Level level, Object message) {
		jdkLogger.log(level, String.valueOf(message));
	}

	private void log(Level level, Object message, Throwable t) {
		jdkLogger.log(level, String.valueOf(message), t);
	}
}
