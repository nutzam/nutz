package org.nutz.el2.arithmetic;

import org.nutz.lang.util.Context;

/**
 * EL参数缓存,用于预编译
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ElCache {
	private Context context;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
}
