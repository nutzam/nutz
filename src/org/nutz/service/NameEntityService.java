package org.nutz.service;

import org.nutz.dao.Dao;

public abstract class NameEntityService<T> extends EntityService<T> {

	protected NameEntityService() {
		super();
	}

	protected NameEntityService(Dao dao) {
		super(dao);
	}

	public void delete(String name) {
		dao().delete(getEntityClass(), name);
	}

	public T fetch(String name) {
		return dao().fetch(getEntityClass(), name);
	}

}
