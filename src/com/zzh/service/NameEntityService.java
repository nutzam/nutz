package com.zzh.service;

import com.zzh.dao.Dao;

public abstract class NameEntityService<T> extends EntityService<T> {

	protected NameEntityService() {
		super();
	}

	public NameEntityService(Dao dao) {
		super(dao);
	}

	public void delete(String name) {
		dao().delete(getEntityClass(), name);
	}

	public T fetch(String name) {
		return dao().fetch(getEntityClass(), name);
	}

}
