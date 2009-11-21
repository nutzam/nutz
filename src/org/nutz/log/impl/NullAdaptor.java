package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;

public class NullAdaptor implements LogAdapter {

	public static NullLog log = new NullLog();

	public boolean canWork() {
		return false;
	}

	public Log getLogger(String className) throws Exception {
		return log;
	}

}
