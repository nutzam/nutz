package org.nutz.log;

public interface Log {
	
	boolean isFatalEnabled();
	
	void fatal(Object message);
	
	void fatal(Object message, Throwable t);
	
	boolean isErrorEnabled();
	
	void error(Object message);
	
	void error(Object message, Throwable t);
	
	boolean isWarnEnabled();
	
	void warn(Object message);
	
	void warn(Object message, Throwable t);
	
	boolean isInfoEnabled();
	
	void info(Object message);
	
	void info(Object message, Throwable t);
	
	boolean isDebugEnabled();
	
	void debug(Object message);
	
	void debug(Object message, Throwable t);

	boolean isTraceEnabled();
	
	void trace(Object message);
	
	void trace(Object message, Throwable t);
}
