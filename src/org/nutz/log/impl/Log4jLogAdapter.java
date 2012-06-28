package org.nutz.log.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;

/**
 * Apache log4j 适配器
 * 
 * <p/>存在<code>org.apache.log4j.Logger</code>就认为可用.
 * <p/>同样的,如果存在log4j-over-slf4j,则也会认为可用.
 * <p/>参考Issue : http://code.google.com/p/nutz/issues/detail?id=322
 * <p/>
 * <b>Log4J 1.2.11及之前的版本不支持Trace级别,默认转为使用Debug级别来Log</b>
 * 
 * @author Young(sunonfire@gmail.com)
 * @author wendal(wendal11985@gmail.com)
 */
public class Log4jLogAdapter implements LogAdapter, Plugin {

    public boolean canWork() {
        try {
            Class.forName("org.apache.log4j.Logger", false, Log4jLogAdapter.class.getClassLoader());
            return true;
        }
        catch (Throwable e) {}
        return false;
    }

    public Log getLogger(String className) {
        return new Log4JLogger(className);
    }

    static class Log4JLogger extends AbstractLog {

        public static final String SUPER_FQCN = AbstractLog.class.getName();
        public static final String SELF_FQCN = Log4JLogger.class.getName();

        private Logger logger;

        private static boolean hasTrace;

        static {
            try {
                Level.class.getDeclaredField("TRACE");
                hasTrace = true;
            }
            catch (Throwable e) {}
        }

        Log4JLogger(String className) {
            logger = Logger.getLogger(className);
            isFatalEnabled = logger.isEnabledFor(Level.FATAL);
            isErrorEnabled = logger.isEnabledFor(Level.ERROR);
            isWarnEnabled = logger.isEnabledFor(Level.WARN);
            isInfoEnabled = logger.isEnabledFor(Level.INFO);
            isDebugEnabled = logger.isEnabledFor(Level.DEBUG);
            if (hasTrace)
                isTraceEnabled = logger.isEnabledFor(Level.TRACE);
        }

        public void debug(Object message, Throwable t) {
            if (isDebugEnabled())
                logger.log(SELF_FQCN, Level.DEBUG, message, t);
        }

        public void error(Object message, Throwable t) {
            if (isErrorEnabled())
                logger.log(SELF_FQCN, Level.ERROR, message, t);

        }

        public void fatal(Object message, Throwable t) {
            if (isFatalEnabled())
                logger.log(SELF_FQCN, Level.FATAL, message, t);
        }

        public void info(Object message, Throwable t) {
            if (isInfoEnabled())
                logger.log(SELF_FQCN, Level.INFO, message, t);
        }

        public void trace(Object message, Throwable t) {
            if (isTraceEnabled())
                logger.log(SELF_FQCN, Level.TRACE, message, t);
            else if ((!hasTrace) && isDebugEnabled())
                logger.log(SELF_FQCN, Level.DEBUG, message, t);
        }

        public void warn(Object message, Throwable t) {
            if (isWarnEnabled())
                logger.log(SELF_FQCN, Level.WARN, message, t);
        }

        @Override
        protected void log(int level, Object message, Throwable tx) {
            switch (level) {
            case LEVEL_FATAL:
                logger.log(SUPER_FQCN, Level.FATAL, message, tx);
                break;
            case LEVEL_ERROR:
                logger.log(SUPER_FQCN, Level.ERROR, message, tx);
                break;
            case LEVEL_WARN:
                logger.log(SUPER_FQCN, Level.WARN, message, tx);
                break;
            case LEVEL_INFO:
                logger.log(SUPER_FQCN, Level.INFO, message, tx);
                break;
            case LEVEL_DEBUG:
                logger.log(SUPER_FQCN, Level.DEBUG, message, tx);
                break;
            case LEVEL_TRACE:
                if (hasTrace)
                    logger.log(SUPER_FQCN, Level.TRACE, message, tx);
                else
                    logger.log(SUPER_FQCN, Level.DEBUG, message, tx);
                break;
            default:
                break;
            }
        }
    }
}
