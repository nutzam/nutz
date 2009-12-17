package org.nutz.log.impl;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.nutz.log.Log;

/**
 * apache log4j 适配器。 判断log4j是否可用的逻辑是依据log4j自身判断逻辑设计的，简单来说：
 * <p>
 * 
 * 1. 如果CurrentThread的ContextClassLoader不能加载类{@link org.apache.log4j.Logger}
 * ，认为log4j不可用；
 * <p>
 * 
 * 2. 如果系统属性log4j.defaultInitOverride非空且非false,log4j不可用；
 * <p>
 * 
 * 3. 如果系统属性log4j.configuration非空，认为log4j可用（有一种情况是log4j.coniguration
 * 中指定的配置文件找不到。我们认为这是一个不应该被忽视的配置错误，所以这里将log4j设定为可用；
 * 这样在实际输出log时log4j就会抛出这个错误，由用户来修正配置）。
 * <p>
 * 
 * 4. 根据log4j的判断逻辑去找log4j.xml或log4j.properties是否存在，如果存在就认为log4j可用。
 * 
 * <b>Log4J 1.2.11及之前的版本不支持Trace级别,默认转为使用Debug级别来Log</b>
 * 
 * @author Young(sunonfire@gmail.com)
 * @author wendal(wendal11985@gmail.com)
 */
public class Log4jLogAdapter extends AbstractLogAdapter{

	public static final String LOG4J_CLASS_NAME = "org.apache.log4j.Logger";
	
	public boolean canWork() {
		try {
			Class.forName(LOG4J_CLASS_NAME, true, Thread.currentThread()
					.getContextClassLoader());
		} catch (ClassNotFoundException e) {
			return false;
		}

		return isPropertyFileConfigured();
	}

	final private boolean isPropertyFileConfigured() {
		String configureValue = System.getProperty("log4j.defaultInitOverride");

		if (configureValue != null && !"false".equalsIgnoreCase(configureValue))
			return false;

		if (System.getProperty("log4j.configuration") != null)
			return true;

		if (canFindInLog4jManner("log4j.properties"))
			return true;

		return canFindInLog4jManner("log4j.xml");
	}

	/**
	 * 本函数仿照log4j检查配置文件能否找到的逻辑。
	 * <p>
	 * 1. 能否由当前线程的ContextClassLoader的getResource找到；
	 * <p>
	 * 2. 能否由加载Log4jAdapter的ClassLoader的getResource方法找到;
	 * <p>
	 * 3. 能否由ClassLoader.getSystemResource找到；
	 * <p>
	 * 
	 * 省略了原函数中关于java 1版本的处理。
	 * <p>
	 * 
	 * @param resourceName
	 *            : 被检查的资源名字。
	 * 
	 * @see org.apache.log4j.helpers.Loader.getResource(String resource)
	 */
	final private boolean canFindInLog4jManner(String resourceName) {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		if (classLoader.getResource(resourceName) != null)
			return true;

		classLoader = this.getClass().getClassLoader();

		if (classLoader.getResource(resourceName) != null)
			return true;

		return (ClassLoader.getSystemResource(resourceName) != null);
	}
	
	public Log getLogger(String className){
		return new Log4JLogger(className);
	}
	
	
	static class Log4JLogger extends AbstractLog{
		
		private Logger logger;
		
		private boolean noTrace = false;
		
		Log4JLogger(){
			logger = LogManager.getRootLogger();
			loadLevel();
		}
		
		Log4JLogger(String className) {
			logger = LogManager.getLogger(className);
			loadLevel();
		}
		
		private void loadLevel(){
			isFatalEnabled = logger.isEnabledFor(Level.FATAL);
			isErrorEnabled = logger.isEnabledFor(Level.ERROR);
			isWarnEnabled = logger.isEnabledFor(Level.WARN);
			isInfoEnabled = logger.isEnabledFor(Level.INFO);
			isDebugEnabled = logger.isEnabledFor(Level.DEBUG);
			try{
				isTraceEnabled = logger.isEnabledFor(Level.TRACE);
			}catch (Throwable e) {
				isTraceEnabled = false;
				noTrace = true;
			}
		}

		public void debug(Object message, Throwable t) {
			if(isDebugEnabled())
				logMessage(Level.DEBUG, message, t);
		}

		public void error(Object message, Throwable t) {
			if(isErrorEnabled())
				logMessage(Level.ERROR, message, t);
			
		}

		public void fatal(Object message, Throwable t) {
			if(isFatalEnabled())
				logMessage(Level.FATAL, message, t);
		}

		public void info(Object message, Throwable t) {
			if(isInfoEnabled())
				logMessage(Level.INFO, message, t);
		}

		public void trace(Object message, Throwable t) {
			if(isTraceEnabled())
				logMessage(Level.TRACE, message, t);
			else if(noTrace)
				error(message, t);
		}

		public void warn(Object message, Throwable t) {
			if(isWarnEnabled())
				logMessage(Level.WARN, message, t);
		}
		
		private void logMessage(Priority priority,Object message, Throwable t){
			if(t != null)
				logger.log(priority, message, t);
			else
				logger.log(priority, message);
		}
	}
}
