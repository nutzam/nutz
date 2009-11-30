package org.nutz.log.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nutz.lang.Mirror;
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
 * @author Young(sunonfire@gmail.com)
 * @author wendal(wendal11985@gmail.com)
 */
public class Log4jAdapter extends AbstractLogAdapter implements Log {

	private static final String GET_EXCEPTION = "get exception";

	public static final String LOG4J_CLASS_NAME = "org.apache.log4j.Logger";

	Object log4jImpl = null;

	static Method fatalObjectMethod = null;

	static Method fatalObjectThrowableMethod = null;

	static Method errorObjectMethod = null;

	static Method errorObjectThrowableMethod = null;

	static Method warnObjectMethod = null;

	static Method warnObjectThrowableMethod = null;

	static Method infoObjectMethod = null;

	static Method infoObjectThrowableMethod = null;

	static Method debugObjectMethod = null;

	static Method debugObjectThrowableMethod = null;

	static Method traceObjectMethod = null;

	static Method traceObjectThrowableMethod = null;

	protected static Class<?> logClass = null;

	private static Mirror<?> log4jMirror = null;

	protected static Mirror<?> levelMirror = null;

	private static Object levelFatal = null;

	private static Object levelError = null;

	private static Object levelWarn = null;

	private static Object levelInfo = null;

	private static Object levelDebug = null;

	private static Method getLogger = null;
	
	private static Method isEnabledFor = null;

	private static Method isTraceEnabledMethod = null;

	public Log4jAdapter() {
	}

	private Log4jAdapter(String className) throws ClassNotFoundException, NoSuchMethodException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		if (logClass == null)
			logClass = Class.forName(LOG4J_CLASS_NAME, true, Thread.currentThread().getContextClassLoader());

		if (log4jMirror == null)
			log4jMirror = Mirror.me(logClass);

		if (getLogger == null)
			getLogger = log4jMirror.findMethod("getLogger", String.class);
		
		log4jImpl = getLogger.invoke(null, className);

		initLevelStuff();
	}

	private void initLevelStuff()
			throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		if (levelMirror == null)
			levelMirror = Mirror.me(Thread.currentThread().getContextClassLoader().loadClass("org.apache.log4j.Level"));

		if (levelFatal == null)
			levelFatal = levelMirror.getField("FATAL").get(log4jImpl);
		
		if (levelError == null)
			levelError = levelMirror.getField("ERROR").get(log4jImpl);
		
		if (levelWarn == null)
			levelWarn = levelMirror.getField("WARN").get(log4jImpl);

		if (levelInfo == null)
			levelInfo = levelMirror.getField("INFO").get(log4jImpl);

		if (levelDebug == null)
			levelDebug = levelMirror.getField("DEBUG").get(log4jImpl);
		
		if (isEnabledFor == null)
			isEnabledFor = log4jMirror.findMethod("isEnabledFor", levelMirror.getType());
		
		isFatalEnabled = ((Boolean) isEnabledFor.invoke(log4jImpl, levelFatal)).booleanValue();

		if (isFatalEnabled) {
			if (fatalObjectMethod == null)
				fatalObjectMethod = log4jMirror.findMethod("fatal", new Class[]{Object.class});

			if (fatalObjectThrowableMethod == null)
				fatalObjectThrowableMethod = log4jMirror.findMethod("fatal", new Class[]{Object.class,
					Throwable.class});
		}

		// error related...
		isErrorEnabled = ((Boolean) isEnabledFor.invoke(log4jImpl, levelError)).booleanValue();

		if (isErrorEnabled) {
			if (errorObjectMethod == null)
				errorObjectMethod = log4jMirror.findMethod("error", new Class[]{Object.class});

			if (errorObjectThrowableMethod == null)
				errorObjectThrowableMethod = log4jMirror.findMethod("error", new Class[]{Object.class,
					Throwable.class});
		}

		// warn related...
		isWarnEnabled = ((Boolean) isEnabledFor.invoke(log4jImpl, levelWarn)).booleanValue();

		if (isWarnEnabled) {
			if (warnObjectMethod == null)
				warnObjectMethod = log4jMirror.findMethod("warn", Object.class);

			if (warnObjectThrowableMethod == null)
				warnObjectThrowableMethod = log4jMirror.findMethod("warn", Object.class,
					Throwable.class);
		}

		// info related...
		isInfoEnabled = ((Boolean) isEnabledFor.invoke(log4jImpl, levelInfo)).booleanValue();

		if (isInfoEnabled) {
			if (infoObjectMethod == null)
				infoObjectMethod = log4jMirror.findMethod("info", new Class[]{Object.class});

			if (infoObjectThrowableMethod == null)
				infoObjectThrowableMethod = log4jMirror.findMethod("info", new Class[]{Object.class,
					Throwable.class});
		}

		// debug related...
		isDebugEnabled = ((Boolean) isEnabledFor.invoke(log4jImpl, levelDebug)).booleanValue();

		if (isDebugEnabled) {

			if (debugObjectMethod == null)
				debugObjectMethod = log4jMirror.findMethod("debug", Object.class);

			if (debugObjectThrowableMethod == null)
				debugObjectThrowableMethod = log4jMirror.findMethod("debug", Object.class,
					Throwable.class);
		}

		// trace related...
		if (isTraceEnabledMethod == null)
			isTraceEnabledMethod = log4jMirror.findMethod("isTraceEnabled");
			
		isTraceEnabled = ((Boolean) isTraceEnabledMethod
				.invoke(log4jImpl)).booleanValue();

		if (isTraceEnabled) {

			if (traceObjectMethod == null)
				traceObjectMethod = log4jMirror.findMethod("trace", Object.class);

			if (traceObjectThrowableMethod == null)
				traceObjectThrowableMethod = log4jMirror.findMethod("trace", Object.class,
					Throwable.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.log.LogAdapter#canWork()
	 */
	public boolean canWork() {
		try {
			Class.forName(LOG4J_CLASS_NAME, true, Thread.currentThread().getContextClassLoader());
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

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (classLoader.getResource(resourceName) != null)
			return true;

		classLoader = this.getClass().getClassLoader();

		if (classLoader.getResource(resourceName) != null)
			return true;

		return (ClassLoader.getSystemResource(resourceName) != null);
	}

	public void debug(Object message) {
		if (isDebugEnabled)
			log(debugObjectMethod, message);
	}

	public void debug(Object message, Throwable t) {
		if (isDebugEnabled)
			log(debugObjectThrowableMethod, message, t);
	}

	public void error(Object message) {
		if (isErrorEnabled)
			log(errorObjectMethod, message);
	}

	public void error(Object message, Throwable t) {
		if (isErrorEnabled)
			log(errorObjectThrowableMethod, message, t);
	}

	public void fatal(Object message) {
		if (isFatalEnabled)
			log(fatalObjectMethod, message);
	}

	public void fatal(Object message, Throwable t) {
		if (isFatalEnabled)
			log(fatalObjectThrowableMethod, message, t);
	}

	public void info(Object message) {
		if (isInfoEnabled) 
			log(infoObjectMethod, message);
	}

	public void info(Object message, Throwable t) {
		if (isInfoEnabled) 
			log(infoObjectThrowableMethod, message, t);
	}

	public void trace(Object message) {
		if (isTraceEnabled)
			log(traceObjectMethod, message);
	}

	public void trace(Object message, Throwable t) {
		if (isTraceEnabled) 
			log(traceObjectThrowableMethod, message, t);
	}

	public void warn(Object message) {
		if (isWarnEnabled) 
			log(warnObjectMethod, message);
	}

	public void warn(Object message, Throwable t) {
		if (isWarnEnabled)
			log(warnObjectThrowableMethod, message, t);
	}

	public Log getLogger(String className) throws ClassNotFoundException, NoSuchMethodException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return new Log4jAdapter(className);
	}


	private void log(Method method,Object message, Throwable t){
		try {
			method.invoke(log4jImpl, message, t);
		} catch (IllegalArgumentException e) {
			systemLog.fatal(GET_EXCEPTION, e);
		} catch (IllegalAccessException e) {
			systemLog.fatal(GET_EXCEPTION, e);
		} catch (InvocationTargetException e) {
			systemLog.fatal(GET_EXCEPTION, e);
		}
	}
	
	private void log(Method method,Object message){
		try {
			method.invoke(log4jImpl, message);
		} catch (IllegalArgumentException e) {
			systemLog.fatal(GET_EXCEPTION, e);
		} catch (IllegalAccessException e) {
			systemLog.fatal(GET_EXCEPTION, e);
		} catch (InvocationTargetException e) {
			systemLog.fatal(GET_EXCEPTION, e);
		}
	}
}
