package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;

/**
 * 一个什么都不做的Log实现,任何Level都返回false
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class NopLog implements Log, LogAdapter {

	@Override
    public Log getLogger(String className) {
		return NOP;
	}
	
	public static final NopLog NOP = new NopLog();
	
	protected NopLog() {
	}
	
	@Override
    public void warnf(String fmt, Object... args) {}
	
	@Override
    public void warn(Object message, Throwable t) {}
	
	@Override
    public void warn(Object message) {}
	
	@Override
    public void tracef(String fmt, Object... args) {}
	
	@Override
    public void trace(Object message, Throwable t) {}
	
	@Override
    public void trace(Object message) {}
	
	@Override
    public boolean isWarnEnabled() {
		return false;
	}
	
	@Override
    public boolean isTraceEnabled() {
		return false;
	}
	
	@Override
    public boolean isInfoEnabled() {
		return false;
	}
	
	@Override
    public boolean isFatalEnabled() {
		return false;
	}
	
	@Override
    public boolean isErrorEnabled() {
		return false;
	}
	
	@Override
    public boolean isDebugEnabled() {
		return false;
	}
	
	@Override
    public void infof(String fmt, Object... args) {
	}
	
	@Override
    public void info(Object message, Throwable t) {
	}
	
	@Override
    public void info(Object message) {
	}
	
	@Override
    public void fatalf(String fmt, Object... args) {
	}
	
	@Override
    public void fatal(Object message, Throwable t) {
	}
	
	@Override
    public void fatal(Object message) {
	}
	
	@Override
    public void errorf(String fmt, Object... args) {
	}
	
	@Override
    public void error(Object message, Throwable t) {
	}
	
	@Override
    public void error(Object message) {
	}
	
	@Override
    public void debugf(String fmt, Object... args) {
	}
	
	@Override
    public void debug(Object message, Throwable t) {
	}
	
	@Override
    public void debug(Object message) {
	}
	
	@Override
    public Log setTag(String tag) {
		return this;
	}
}
