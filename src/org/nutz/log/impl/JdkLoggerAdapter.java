package org.nutz.log.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.nutz.log.Log;

/**
 * JDK Logger的适配器。<p>
 * 如果系统属性java.util.logging.config.class或java.util.logging.config.file属性非空我们就认为用户想用JDK Logger.<p>
 * 
 * JDK Logger的日志级别和Log接口不同，对应关系请参考常量的定义。
 * 
 * 
 * @author Young(sunonfire@gmail.com)
 *
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
	
	public JdkLoggerAdapter() {}

	public void debug(Object message) {
		jdkLogger.log(DEBUG_LEVEL, message.toString());
	}

	public void debug(Object message, Throwable t) {
		jdkLogger.log(DEBUG_LEVEL, message.toString(), t);
	}

	public void error(Object message) {
		jdkLogger.log(ERROR_LEVEL, message.toString());
	}

	public void error(Object message, Throwable t) {
		jdkLogger.log(ERROR_LEVEL, message.toString(), t);
	}

	public void fatal(Object message) {
		jdkLogger.log(FATAL_LEVEL, message.toString());
	}

	public void fatal(Object message, Throwable t) {
		
		jdkLogger.log(FATAL_LEVEL, message.toString(), t);
	}

	public void info(Object message) {
		jdkLogger.log(INFO_LEVEL, message.toString());
	}

	public void info(Object message, Throwable t) {
		jdkLogger.log(INFO_LEVEL, message.toString(), t);
	}

	public void trace(Object message) {
		jdkLogger.log(TRACE_LEVEL, message.toString());
	}

	public void trace(Object message, Throwable t) {
		jdkLogger.log(TRACE_LEVEL, message.toString(), t);
	}

	public void warn(Object message) {
		jdkLogger.log(WARN_LEVEL, message.toString());
	}

	public void warn(Object message, Throwable t) {
		jdkLogger.log(WARN_LEVEL, message.toString(), t);
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

}
