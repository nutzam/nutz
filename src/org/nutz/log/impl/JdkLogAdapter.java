package org.nutz.log.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;

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
public class JdkLogAdapter implements LogAdapter, Plugin {

	public boolean canWork() {
		return System.getProperty("java.util.logging.config.class") != null
        		|| System.getProperty("java.util.logging.config.file") != null;
	}
	
	public Log getLogger(String className){
		return new JdkLogger(className);
	}
	
	static class JdkLogger extends AbstractLog {

		private Logger jdkLogger = null;

		public JdkLogger(String className) {

			jdkLogger = Logger.getLogger(className);

			isFatalEnabled = jdkLogger.isLoggable(Level.SEVERE);
			isErrorEnabled = jdkLogger.isLoggable(Level.SEVERE);
			isWarnEnabled = jdkLogger.isLoggable(Level.WARNING);
			isInfoEnabled = jdkLogger.isLoggable(Level.INFO);
			isDebugEnabled = jdkLogger.isLoggable(Level.FINE);
			isTraceEnabled = jdkLogger.isLoggable(Level.FINEST);
		}

		public void fatal(Object message, Throwable t) {
			if (isFatalEnabled())
				log(LEVEL_FATAL, message, t);
		}

		public void error(Object message, Throwable t) {
			if (isErrorEnabled())
				log(LEVEL_ERROR, message, t);
		}

		public void warn(Object message, Throwable t) {
			if (isWarnEnabled())
				log(LEVEL_WARN, message, t);
		}

		public void info(Object message, Throwable t) {
			if (isInfoEnabled())
				log(LEVEL_INFO, message, t);
		}

		public void debug(Object message, Throwable t) {
			if (isDebugEnabled())
				log(LEVEL_DEBUG, message, t);
		}

		public void trace(Object message, Throwable t) {
			if (isTraceEnabled())
				log(LEVEL_TRACE, message, t);
		}

		protected void log(int level_int, Object message, Throwable t) {
			Level level = null;
			switch (level_int) {
			case LEVEL_FATAL:
				level = Level.SEVERE;
				break;
			case LEVEL_ERROR:
				level = Level.SEVERE;
				break;
			case LEVEL_WARN:
				level = Level.WARNING;
				break;
			case LEVEL_INFO:
				level = Level.INFO;
				break;
			case LEVEL_DEBUG:
				level = Level.FINE;
				break;
			case LEVEL_TRACE:
				level = Level.FINEST;
				break;
			default:
				return;
			}
			//From Apache Common Logging 1.1.1
			Throwable dummyException = new Throwable();
            StackTraceElement locations[] = dummyException.getStackTrace();
            String cname = "unknown";
            String method = "unknown";
            if( locations != null && locations.length > 1 ) {
                StackTraceElement caller = locations[2];
                cname = caller.getClassName();
                method = caller.getMethodName();
            }
            if( t == null )
            	jdkLogger.logp( level, cname, method, String.valueOf(message) );
            else 
            	jdkLogger.logp( level, cname, method, String.valueOf(message), t );
		}
	}

}
