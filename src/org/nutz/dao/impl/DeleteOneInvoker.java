package org.nutz.dao.impl;

import org.nutz.dao.entity.Link;

class DeleteOneInvoker extends DeleteInvoker {

	DeleteOneInvoker(NutDao dao) {
		super(dao);
	}

	void invoke(Link link, Object one) {
		dao.delete(one);
	}

}
