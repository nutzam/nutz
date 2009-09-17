package org.nutz.dao.impl;

abstract class DeleteInvoker extends LinkInvoker {

	protected NutDao dao;

	DeleteInvoker(NutDao dao) {
		this.dao = dao;
	}

}
