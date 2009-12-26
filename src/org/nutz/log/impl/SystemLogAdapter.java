package org.nutz.log.impl;

import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;

public class SystemLogAdapter implements LogAdapter, Plugin {

	public Log getLogger(String className) {
		return SystemLog.me();
	}

	public boolean canWork() {
		return true;
	}

}
