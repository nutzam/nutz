package org.nutz.http.impl;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class HttpObject {
	
	protected SessionManger sessionManger;
	
	//----------------------------------------------------------------
	protected Context ctx = Lang.context();
	public Context ctx() {
		return ctx;
	}
}
