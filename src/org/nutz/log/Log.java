package org.nutz.log;

/**
 * 日志接口
 * 
 * @author Young(sunonfire@gmail.com)
 */
public interface Log {

	boolean isErrorEnabled();
	boolean isWarnEnabled();
	boolean isInfoEnabled();
	boolean isDebugEnabled();
	boolean isTraceEnabled();
	
	void error(Object...infos);
	void warn(Object...infos);
	void info(Object...infos);
	void debug(Object...infos);
	void trace(Object...infos);
	
	/*以下方法不建议使用,日后可能会删除*/
	void errorf(Object...infos);
	void warnf(Object...infos);
	void infof(Object...infos);
	void debugf(Object...infos);
	void tracef(Object...infos);
}
