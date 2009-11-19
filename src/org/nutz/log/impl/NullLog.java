package org.nutz.log.impl;

import org.nutz.log.Log;

/**
 * 没有Log可以用的时候的空Log
 * @author Young(sunonfire@gmail.com)
 *
 */
public class NullLog implements Log {

	public void debug(Object message) {
	}

	public void debug(Object message, Throwable t) {
	}

	public void error(Object message) {
	}

	public void error(Object message, Throwable t) {
	}

	public void fatal(Object message) {
	}

	public void fatal(Object message, Throwable t) {
	}

	public void info(Object message) {
	}

	public void info(Object message, Throwable t) {
	}

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

	public void trace(Object message) {
	}

	public void trace(Object message, Throwable t) {
	}

	public void warn(Object message) {
	}

	public void warn(Object message, Throwable t) {
	}

}
