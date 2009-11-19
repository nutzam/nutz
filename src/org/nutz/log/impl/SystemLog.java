package org.nutz.log.impl;

import org.nutz.lang.Lang;
import org.nutz.log.Log;

/**
 * 用来做在Log内部处理时的日志输出。到System.err.
 * @author Young(sunonfire@gmail.com)
 *
 */
public class SystemLog implements Log {

	public void debug(Object message) {
		throw Lang.noImplement();
	}

	public void debug(Object message, Throwable t) {
		throw Lang.noImplement();
	}

	public void error(Object message) {
		throw Lang.noImplement();
	}

	public void error(Object message, Throwable t) {
		throw Lang.noImplement();
	}

	public void fatal(Object message) {
		System.err.println(message);
	}

	public void fatal(Object message, Throwable t) {
		System.err.println(message);
		System.err.println(t.getMessage());
		t.printStackTrace(System.err);
	}

	public void info(Object message) {
		throw Lang.noImplement();
	}

	public void info(Object message, Throwable t) {
		throw Lang.noImplement();
	}

	public boolean isDebugEnabled() {
		throw Lang.noImplement();
	}

	public boolean isErrorEnabled() {
		throw Lang.noImplement();
	}

	public boolean isFatalEnabled() {
		throw Lang.noImplement();
	}

	public boolean isInfoEnabled() {
		throw Lang.noImplement();
	}

	public boolean isTraceEnabled() {
		throw Lang.noImplement();
	}

	public boolean isWarnEnabled() {
		throw Lang.noImplement();
	}

	public void trace(Object message) {
		throw Lang.noImplement();
	}

	public void trace(Object message, Throwable t) {
		throw Lang.noImplement();
	}

	public void warn(Object message) {
		throw Lang.noImplement();
	}

	public void warn(Object message, Throwable t) {
		throw Lang.noImplement();
	}

}
