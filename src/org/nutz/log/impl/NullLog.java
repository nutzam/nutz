package org.nutz.log.impl;

/**
 * 没有任何输出的空Log
 * 
 * @author Young(sunonfire@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class NullLog extends AbstractLog {

	public boolean isDebugEnabled() {
		return false;
	}

	public boolean isErrorEnabled() {
		return false;
	}

	public boolean isFatalEnabled() {
		return false;
	}

	public boolean isInfoEnabled() {
		return false;
	}

	public boolean isTraceEnabled() {
		return false;
	}

	public boolean isWarnEnabled() {
		return false;
	}

	public void debug(Object message, Throwable t) {
	}

	public void error(Object message, Throwable t) {
	}

	public void fatal(Object message, Throwable t) {
	}

	public void info(Object message, Throwable t) {
	}

	public void trace(Object message, Throwable t) {
	}

	public void warn(Object message, Throwable t) {
	}
}
