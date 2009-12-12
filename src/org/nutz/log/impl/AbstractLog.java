package org.nutz.log.impl;

import org.nutz.log.Log;

public abstract class AbstractLog implements Log {
	
	protected boolean isFatalEnabled = true;
	protected boolean isErrorEnabled = true;
	protected boolean isWarnEnabled  = true;
	protected boolean isInfoEnabled  = false;
	protected boolean isDebugEnabled = false;
	protected boolean isTraceEnabled = false;


	public void debug(Object message) {
		debug(message, null);
	}

	public void debugf(String fmt, Object... args) {
		debug(String.format(fmt, args));
	}

	public void error(Object message) {
		error(message, null);
	}

	public void errorf(String fmt, Object... args) {
		error(String.format(fmt, args));
	}

	public void fatal(Object message) {
		fatal(message, null);
	}

	public void fatalf(String fmt, Object... args) {
		fatal(String.format(fmt, args));
	}

	public void info(Object message) {
		info(message, null);
	}

	public void infof(String fmt, Object... args) {
		info(String.format(fmt, args));
	}
	

	public void trace(Object message) {
		trace(message, null);
	}

	public void tracef(String fmt, Object... args) {
		trace(String.format(fmt, args));
	}

	public void warn(Object message) {
		warn(message, null);
	}

	public void warnf(String fmt, Object... args) {
		warn(String.format(fmt, args));
	}

	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	public boolean isErrorEnabled() {
		return isErrorEnabled;
	}

	public boolean isFatalEnabled() {
		return isFatalEnabled;
	}

	public boolean isInfoEnabled() {
		return isInfoEnabled;
	}

	public boolean isTraceEnabled() {
		return isTraceEnabled;
	}

	public boolean isWarnEnabled() {
		return isWarnEnabled;
	}

}
