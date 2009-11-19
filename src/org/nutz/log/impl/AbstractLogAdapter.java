package org.nutz.log.impl;

import org.nutz.log.LogAdapter;

public abstract class AbstractLogAdapter implements LogAdapter {

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

}