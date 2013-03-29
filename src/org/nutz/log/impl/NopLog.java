package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;

/**
 * 一个什么都不做的Log实现,任何Level都返回false
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class NopLog implements Log, LogAdapter {

	public Log getLogger(String className) {
		return NOP;
	}
	
	public static final NopLog NOP = new NopLog();
	
	protected NopLog() {
	}
	
	public void warnf(String fmt, Object... args) {}
	
	public void warn(Object message, Throwable t) {}
	
	public void warn(Object message) {}
	
	public void tracef(String fmt, Object... args) {}
	
	public void trace(Object message, Throwable t) {}
	
	public void trace(Object message) {}
	
	public boolean isWarnEnabled() {
		return false;
	}
	
	public boolean isTraceEnabled() {
		return false;
	}
	
	public boolean isInfoEnabled() {
		return false;
	}
	
	public boolean isFatalEnabled() {
		return false;
	}
	
	public boolean isErrorEnabled() {
		return false;
	}
	
	public boolean isDebugEnabled() {
		return false;
	}
	
	public void infof(String fmt, Object... args) {
	}
	
	public void info(Object message, Throwable t) {
	}
	
	public void info(Object message) {
	}
	
	public void fatalf(String fmt, Object... args) {
	}
	
	public void fatal(Object message, Throwable t) {
	}
	
	public void fatal(Object message) {
	}
	
	public void errorf(String fmt, Object... args) {
	}
	
	public void error(Object message, Throwable t) {
	}
	
	public void error(Object message) {
	}
	
	public void debugf(String fmt, Object... args) {
	}
	
	public void debug(Object message, Throwable t) {
	}
	
	public void debug(Object message) {
	}
	
	public Log setTag(String tag) {
		return this;
	}
}
