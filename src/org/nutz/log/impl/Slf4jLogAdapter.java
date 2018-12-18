package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * 让Nutz的日志走Slf4j的API
 */
public class Slf4jLogAdapter implements LogAdapter, Plugin {

    public Log getLogger(String className) {
        Logger logger = LoggerFactory.getLogger(className);
        if (logger instanceof LocationAwareLogger)
            return new Slf4jLogger((LocationAwareLogger) logger);
        // 总有人用slf4j-simple的? 成全他们吧
        return new SimpleSlf4jLogger(logger);
    }

    static class SimpleSlf4jLogger extends AbstractLog {
        protected Logger logger;

        public SimpleSlf4jLogger(Logger logger) {
            this.logger = logger;
        }

        public void warn(Object message, Throwable t) {
            logger.warn(String.valueOf(message), t);
        }

        public void trace(Object message, Throwable t) {
            logger.trace(String.valueOf(message), t);
        }

        public void info(Object message, Throwable t) {
            logger.info(String.valueOf(message), t);
        }

        public void fatal(Object message, Throwable t) {
            logger.error(String.valueOf(message), t);
        }

        public void error(Object message, Throwable t) {
            logger.error(String.valueOf(message), t);
        }

        public void debug(Object message, Throwable t) {
            logger.debug(String.valueOf(message), t);
        }

        protected void log(int level, Object message, Throwable tx) {
            String msg = String.valueOf(message);
            switch (level) {
            case AbstractLog.LEVEL_FATAL:
            case AbstractLog.LEVEL_ERROR:
                logger.error(msg, tx);
                break;
            case AbstractLog.LEVEL_WARN:
                logger.warn(msg, tx);
                break;
            case AbstractLog.LEVEL_INFO:
                logger.info(msg, tx);
                break;
            case AbstractLog.LEVEL_TRACE:
                logger.trace(msg, tx);
                break;
            case AbstractLog.LEVEL_DEBUG:
            default:
                logger.debug(msg, tx);
                break;
            }
        }
    }

    public boolean canWork() {
        try {
            Logger.class.getName();
            return true;
        }
        catch (Throwable e) {}
        return false;
    }

}
