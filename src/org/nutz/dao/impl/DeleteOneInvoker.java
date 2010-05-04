package org.nutz.dao.impl;

import org.nutz.dao.entity.Link;

public class DeleteOneInvoker extends DeleteInvoker {

	DeleteOneInvoker(NutDao dao) {
		super(dao);
	}

	public void invoke(Link link, Object one) {
		dao.delete(one);
	}

}
