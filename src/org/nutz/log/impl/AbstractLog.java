package org.nutz.log.impl;

import java.util.Arrays;

import org.nutz.log.Log;

public abstract class AbstractLog implements Log {

    protected boolean isFatalEnabled = true;
    protected boolean isErrorEnabled = true;
    protected boolean isWarnEnabled = true;
    protected boolean isInfoEnabled = false;
    protected boolean isDebugEnabled = false;
    protected boolean isTraceEnabled = false;

    protected static final int LEVEL_FATAL = 50;
    protected static final int LEVEL_ERROR = 40;
    protected static final int LEVEL_WARN = 30;
    protected static final int LEVEL_INFO = 20;
    protected static final int LEVEL_DEBUG = 10;
    protected static final int LEVEL_TRACE = 0;
    
    protected abstract void log(int level, Object message, Throwable tx);
    
    protected void log(int level, LogInfo info){
        log(level, info.message, info.e);
    }

    private static final LogInfo LOGINFO_ERROR = new LogInfo();
    private static final LogInfo LOGINFO_NULL = new LogInfo();
    static{
        LOGINFO_ERROR.message = "!!!!Log Fail!!";
        LOGINFO_NULL.message = "null";
    }
    
    /**
     * 产生一个LogInfo对象,以支持以下调用方式:
     * <p/><code>log.warn(e)</code>
     * <p/><code>log.warnf("User(name=%s) login fail",username,e)</code>
     */
    private LogInfo makeInfo(Object obj, Object... args) {
        if (obj == null)
            return LOGINFO_NULL;
        try {
            LogInfo info = new LogInfo();
            if (obj instanceof Throwable) {
                info.e = (Throwable)obj;
                info.message = info.e.getMessage();
            }
            else if (args == null || args.length == 0) {
                info.message = obj.toString();
            }
//            //map to another mehtod
//            else if (args.length == 1 && args[0] instanceof Throwable) {
//                info.message = obj.toString();
//                info.e = (Throwable)args[0];
//            }
            else {
                info.message = String.format(obj.toString(), args);
                if (args[args.length - 1] instanceof Throwable)
                    info.e = (Throwable) args[args.length - 1];
            }
            return info;
        }
        catch (Throwable e) { //即使格式错误也继续log
            if (isWarnEnabled())
                warn("String format fail in log , fmt = "+ obj + " , args = " +Arrays.toString(args),e);
            return LOGINFO_ERROR;
        }
    }

    public void debug(Object message) {
        if (isDebugEnabled())
            log(LEVEL_DEBUG, makeInfo(message));
    }

    public void debugf(String fmt, Object... args) {
        if (isDebugEnabled())
            log(LEVEL_DEBUG, makeInfo(fmt, args));
    }

    public void error(Object message) {
        if (isErrorEnabled())
            log(LEVEL_ERROR, makeInfo(message));
    }

    public void errorf(String fmt, Object... args) {
        if (isErrorEnabled())
            log(LEVEL_ERROR, makeInfo(fmt, args));
    }

    public void fatal(Object message) {
        if (isFatalEnabled())
            log(LEVEL_FATAL, makeInfo(message));
    }

    public void fatalf(String fmt, Object... args) {
        if (isFatalEnabled())
            log(LEVEL_FATAL, makeInfo(fmt, args));
    }

    public void info(Object message) {
        if (isInfoEnabled())
            log(LEVEL_INFO, makeInfo(message));
    }

    public void infof(String fmt, Object... args) {
        if (isInfoEnabled())
            log(LEVEL_INFO, makeInfo(fmt, args));
    }

    public void trace(Object message) {
        if (isTraceEnabled())
            log(LEVEL_TRACE, makeInfo(message));
    }

    public void tracef(String fmt, Object... args) {
        if (isTraceEnabled())
            log(LEVEL_TRACE, makeInfo(fmt, args));
    }

    public void warn(Object message) {
        if (isWarnEnabled())
            log(LEVEL_WARN, makeInfo(message));
    }

    public void warnf(String fmt, Object... args) {
        if (isWarnEnabled())
            log(LEVEL_WARN, makeInfo(fmt, args));
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

    protected String tag = "";
    
    public Log setTag(String tag) {
    	this.tag = tag;
    	return this;
    }
}
