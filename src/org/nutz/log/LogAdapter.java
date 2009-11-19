package org.nutz.log;

public interface LogAdapter {

	public abstract boolean canWork();

	public Log getLogger(String className) throws Exception;
}