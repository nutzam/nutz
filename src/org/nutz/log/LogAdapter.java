package org.nutz.log;

/**
 * @author Young(sunonfire@gmail.com)
 */
public interface LogAdapter {

	boolean canWork();

	Log getLogger(String className) throws Exception;
}