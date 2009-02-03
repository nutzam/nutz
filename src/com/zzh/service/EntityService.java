package com.zzh.service;

import java.util.List;

import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.lang.Mirror;
import com.zzh.lang.meta.Pager;

public abstract class EntityService<T> extends Service {

	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	protected EntityService() {
		entityClass = (Class<T>) Mirror.getTypeParams(getClass())[0];
	}

	protected EntityService(Dao dao) {
		this();
		this.setDao(dao);
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void clear(Condition condition) {
		dao().clear(entityClass, condition);
	}

	public void clearMany(Object obj, String fieldName) {
		dao().clearMany(obj, fieldName);
	}

	public T insert(T obj) {
		return dao().insert(obj);
	}

	public void delete(Object obj) {
		dao().delete(obj);
	}
	
	public List<T> query(Condition condition, Pager pager) {
		return (List<T>) dao().query(entityClass, condition, pager);
	}

	public T update(T obj) {
		return dao().update(obj);
	}

	public int count(Condition condition) {
		return dao().count(entityClass, condition);
	}

	public int count() {
		return dao().count(entityClass);
	}

}
