package org.nutz.dao.impl;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Mirror;

class InsertOneInvoker extends InsertInvoker {

	public InsertOneInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
		super(dao, mainObj, mirror);
	}

	void invoke(Link link, Object one) {
		one = this.dao.insert(one);
		Mirror<?> ta = Mirror.me(one.getClass());
		Object value = ta.getValue(one, link.getTargetField());
		mirror.setValue(mainObj, link.getReferField(), value);
	}
}
