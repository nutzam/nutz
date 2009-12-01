package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;

/**
 * @author Young(sunonfire@gmail.com)
 */
public abstract class AbstractLogAdapter implements LogAdapter, Log {

	protected static final SystemLog systemLog = new SystemLog();
	protected boolean isFatalEnabled = true;
	protected boolean isErrorEnabled = true;
	protected boolean isWarnEnabled = true;
	protected boolean isInfoEnabled = false;
	protected boolean isDebugEnabled = false;
	protected boolean isTraceEnabled = false;

	public AbstractLogAdapter() {
		super();
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

	public void fatalf(String fmt, Object...args) {
		if (isFatalEnabled)
			fatal(String.format(fmt, args));
	}
	
	public void errorf(String fmt, Object...args) {
		if (isErrorEnabled)
			error(String.format(fmt, args));
	}
	
	public void warnf(String fmt, Object... args) {
		if (isWarnEnabled)
			warn(String.format(fmt, args));
	}
	
	public void debugf(String fmt, Object... args) {
		if (isDebugEnabled)
			debug(String.format(fmt, args));
	}

	public void infof(String fmt, Object... args) {
		if (isInfoEnabled)
			info(String.format(fmt, args));
	}

	public void tracef(String fmt, Object... args) {
		if (isTraceEnabled)
			trace(String.format(fmt, args));
	}
	
	

}