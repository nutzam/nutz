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
 */
public class Log4jAdapter extends AbstractLogAdapter implements Log {

	public static final String LOG4J_CLASS_NAME = "org.apache.log4j.Logger";

	Object log4jImpl = null;

	Method fatalObjectMethod = null;

	Method fatalObjectThrowableMethod = null;

	Method errorObjectMethod = null;

	Method errorObjectThrowableMethod = null;

	Method warnObjectMethod = null;

	Method warnObjectThrowableMethod = null;

	Method infoObjectMethod = null;

	Method infoObjectThrowableMethod = null;

	Method debugObjectMethod = null;

	Method debugObjectThrowableMethod = null;

	Method traceObjectMethod = null;

	Method traceObjectThrowableMethod = null;

	public Log4jAdapter() {

	}

	private Log4jAdapter(String className) throws ClassNotFoundException, NoSuchMethodException,
			NoSuchFieldException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		Class<?> logClass = Class.forName(LOG4J_CLASS_NAME, true, classLoader);

		Mirror<?> log4jMirror = Mirror.me(logClass);

		log4jImpl = log4jMirror.invoke(null, "getLogger", new Object[]{className});

		initLevelStuff(classLoader, log4jMirror);
	}

	private void initLevelStuff(ClassLoader classLoader, Mirror<?> log4jMirror)
			throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
		
		Mirror<?> priorityMirror = Mirror.me(classLoader.loadClass("org.apache.log4j.Priority"));

		// fatal related...
		isFatalEnabled = ((Boolean) log4jMirror.invoke(log4jImpl, "isEnabledFor", priorityMirror
				.getField("FATAL"))).booleanValue();

		if (isFatalEnabled) {
			fatalObjectMethod = log4jMirror.findMethod("fatal", new Class[]{Object.class});

			fatalObjectThrowableMethod = log4jMirror.findMethod("fatal", new Class[]{Object.class,
					Throwable.class});
		}

		// error related...
		isErrorEnabled = ((Boolean) log4jMirror.invoke(log4jImpl, "isEnabledFor", priorityMirror
				.getField("ERROR"))).booleanValue();

		if (isErrorEnabled) {
			errorObjectMethod = log4jMirror.findMethod("error", new Class[]{Object.class});

			errorObjectThrowableMethod = log4jMirror.findMethod("error", new Class[]{Object.class,
					Throwable.class});
		}

		// warn related...
		isWarnEnabled = ((Boolean) log4jMirror.invoke(log4jImpl, "isEnabledFor", priorityMirror
				.getField("WARN"))).booleanValue();

		if (isWarnEnabled) {
			warnObjectMethod = log4jMirror.findMethod("warn", Object.class);

			warnObjectThrowableMethod = log4jMirror.findMethod("warn", Object.class,
					Throwable.class);
		}

		// info related...
		isInfoEnabled = ((Boolean) log4jMirror.invoke(log4jImpl, "isEnabledFor", priorityMirror
				.getField("INFO"))).booleanValue();

		if (isInfoEnabled) {
			infoObjectMethod = log4jMirror.findMethod("info", new Class[]{Object.class});

			infoObjectThrowableMethod = log4jMirror.findMethod("info", new Class[]{Object.class,
					Throwable.class});
		}

		// debug related...
		isDebugEnabled = ((Boolean) log4jMirror.invoke(log4jImpl, "isEnabledFor", priorityMirror
				.getField("DEBUG"))).booleanValue();

		if (isDebugEnabled) {

			debugObjectMethod = log4jMirror.findMethod("debug", Object.class);

			debugObjectThrowableMethod = log4jMirror.findMethod("debug", Object.class,
					Throwable.class);
		}

		// trace related...
		isTraceEnabled = ((Boolean) log4jMirror
				.invoke(log4jImpl, "isTraceEnabled", (Object[]) null)).booleanValue();

		if (isTraceEnabled) {

			traceObjectMethod = log4jMirror.findMethod("trace", Object.class);

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
		if (isDebugEnabled) {
			try {
				debugObjectMethod.invoke(log4jImpl, message);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void debug(Object message, Throwable t) {
		if (isDebugEnabled) {
			try {
				debugObjectThrowableMethod.invoke(log4jImpl, message, t);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void error(Object message) {
		if (isErrorEnabled) {
			try {
				errorObjectMethod.invoke(log4jImpl, message);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void error(Object message, Throwable t) {

		if (isErrorEnabled) {
			try {
				errorObjectThrowableMethod.invoke(log4jImpl, message, t);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void fatal(Object message) {
		if (isFatalEnabled) {
			try {
				fatalObjectMethod.invoke(log4jImpl, message);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void fatal(Object message, Throwable t) {
		if (isFatalEnabled) {
			try {
				fatalObjectThrowableMethod.invoke(log4jImpl, message, t);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void info(Object message) {
		if (isInfoEnabled) {
			try {
				infoObjectMethod.invoke(log4jImpl, message);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}
	}

	public void info(Object message, Throwable t) {
		if (isInfoEnabled) {
			try {
				infoObjectThrowableMethod.invoke(log4jImpl, message, t);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}
	}

	public void trace(Object message) {

		if (isTraceEnabled) {
			try {
				traceObjectMethod.invoke(log4jImpl, message);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void trace(Object message, Throwable t) {

		if (isTraceEnabled) {
			try {
				traceObjectThrowableMethod.invoke(log4jImpl, message, t);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void warn(Object message) {

		if (isWarnEnabled) {
			try {
				warnObjectMethod.invoke(log4jImpl, message);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public void warn(Object message, Throwable t) {

		if (isWarnEnabled) {
			try {
				warnObjectThrowableMethod.invoke(log4jImpl, message, t);
			} catch (IllegalArgumentException e) {
				systemLog.fatal("get exception", e);
			} catch (IllegalAccessException e) {
				systemLog.fatal("get exception", e);
			} catch (InvocationTargetException e) {
				systemLog.fatal("get exception", e);
			}
		}

	}

	public Log getLogger(String className) throws ClassNotFoundException, NoSuchMethodException,
			NoSuchFieldException {
		return new Log4jAdapter(className);
	}

}
