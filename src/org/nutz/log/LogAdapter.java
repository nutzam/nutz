package org.nutz.log;

/**
 * @author Young(sunonfire@gmail.com)
 */
public interface LogAdapter {

	public abstract boolean canWork();

	public Log getLogger(String className) throws Exception;
}