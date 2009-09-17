package org.nutz.dao.impl;

import org.nutz.dao.Dao;
import org.nutz.lang.Mirror;

abstract class InsertInvoker extends LinkInvoker {

	Dao dao;
	Object mainObj;
	Mirror<?> mirror;

	public InsertInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
		this.dao = dao;
		this.mainObj = mainObj;
		this.mirror = mirror;
	}

}
