package org.nutz.log.impl;

import java.util.Date;

import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;

public class SystemLogAdapter implements LogAdapter, Plugin {

    @Override
    public Log getLogger(String className) {
        return SystemLog.me();
    }

    @Override
    public boolean canWork() {
        return true;
    }

    /**
     * 默认的Log,输出到System.out和System.err
     * 
     * @author Young(sunonfire@gmail.com)
     * @author Wendal(wendal1985@gmail.com)
     */
    public static class SystemLog extends AbstractLog {

        private final static SystemLog me = new SystemLog();
        
        private static boolean warned;

        static SystemLog me() {
            if (! warned) {
                me.info("Select SystemLog as Nutz.Log implement");
                warned = true;
            }
            return me;
        }

        private SystemLog() {
            isInfoEnabled = true;
            isDebugEnabled = true;
        }

        @Override
        public void debug(Object message, Throwable t) {
            if (isDebugEnabled()) {
                printOut("DEBUG",message, t);
            }
        }

        @Override
        public void error(Object message, Throwable t) {
            if (isErrorEnabled()) {
                errorOut("ERROR",message, t);
            }
        }

        @Override
        public void fatal(Object message, Throwable t) {
            if (isFatalEnabled()) {
                errorOut("FATAL",message, t);
            }
        }

        @Override
        public void info(Object message, Throwable t) {
            if (isInfoEnabled()) {
                printOut("INFO",message, t);
            }
        }

        @Override
        public void trace(Object message, Throwable t) {
            if (isTraceEnabled()) {
                printOut("TRACE",message, t);
            }
        }

        @Override
        public void warn(Object message, Throwable t) {
            if (isWarnEnabled()) {
                errorOut("WARN",message, t);
            }
        }

        private void printOut(String level, Object message, Throwable t) {
            System.out.printf("%s %s [%s] %s\n", Times.sDTms2(new Date()), level, Thread.currentThread().getName(),message);
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }

        private void errorOut(String level, Object message, Throwable t) {
            System.err.printf("%s %s [%s] %s\n", Times.sDTms2(new Date()), level, Thread.currentThread().getName(),message);
            if (t != null) {
                t.printStackTrace(System.err);
            }
        }

        @Override
        protected void log(int level, Object message, Throwable tx) {
            switch (level) {
                case LEVEL_FATAL:
                    fatal(message, tx);
                    break;
                case LEVEL_ERROR:
                    error(message, tx);
                    break;
                case LEVEL_WARN:
                    warn(message, tx);
                    break;
                case LEVEL_INFO:
                    info(message, tx);
                    break;
                case LEVEL_DEBUG:
                    debug(message, tx);
                    break;
                case LEVEL_TRACE:
                    trace(message, tx);
                    break;
                default:
            }
        }

    }
}
