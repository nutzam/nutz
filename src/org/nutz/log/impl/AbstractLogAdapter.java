package org.nutz.log.impl;

import org.nutz.log.LogAdapter;
import org.nutz.plugin.AbstractNutPlugin;

/**
 * 
 * @author Wendal(wendal1985@gmail.com)
 *
 */
public abstract class AbstractLogAdapter extends AbstractNutPlugin implements LogAdapter {

	public Class<?> workFor() {
		return LogAdapter.class;
	}
}
