package org.nutz.service;

import org.nutz.dao.Dao;

public abstract class IdNameEntityService<T> extends IdEntityService<T> {

	protected IdNameEntityService() {
		super();
	}

	protected IdNameEntityService(Dao dao) {
		super(dao);
	}

	public void delete(String name) {
		dao().delete(getEntityClass(), name);
	}

	public T fetch(String name) {
		return dao().fetch(getEntityClass(), name);
	}
}
