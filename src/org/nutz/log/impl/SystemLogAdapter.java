package org.nutz.log.impl;

import org.nutz.log.Log;

public class SystemLogAdapter extends AbstractLogAdapter {

	public Log getLogger(String className) {
		return SystemLog.me();
	}

	public boolean canWork() {
		return true;
	}

}
