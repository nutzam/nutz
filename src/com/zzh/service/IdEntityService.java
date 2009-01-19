package com.zzh.service;

import com.zzh.dao.Dao;

public abstract class IdEntityService<T> extends EntityService<T> {

	protected IdEntityService() {
		super();
	}

	public IdEntityService(Dao dao) {
		super(dao);
	}

	public T fetch(long id) {
		return dao().fetch(getEntityClass(), id);
	}

	public void delete(long id) {
		dao().delete(getEntityClass(), id);
	}

	public int getMaxId() {
		return dao().getMaxId(getEntityClass());
	}

}
