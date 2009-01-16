package com.zzh.service;

import com.zzh.dao.Dao;

public abstract class NutEntityService<T> extends IdEntityService<T> {

	protected NutEntityService() {
		super();
	}

	public NutEntityService(Dao dao) {
		super(dao);
	}

	public void delete(String name) {
		dao().delete(getEntityClass(), name);
	}

	public T fetch(String name) {
		return dao().fetch(getEntityClass(), name);
	}
}
