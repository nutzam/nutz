package org.nutz.log.impl;

import org.nutz.log.LogAdapter;
import org.nutz.plugin.Plugin;

/**
 * 
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
public abstract class AbstractLogAdapter implements LogAdapter, Plugin {

	public Class<?> getWorkType() {
		return LogAdapter.class;
	}

	public void depose() throws Throwable {}

	public void init() throws Throwable {}
}
