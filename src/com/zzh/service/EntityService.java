package com.zzh.service;

import java.util.List;

import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.entity.Entity;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.dao.Pager;

public abstract class EntityService<T> extends Service {

	private Mirror<T> mirror;

	@SuppressWarnings("unchecked")
	protected EntityService() {
		mirror = Mirror.me((Class<T>) Mirror.getTypeParams(getClass())[0]);
	}

	protected EntityService(Dao dao) {
		this();
		this.setDao(dao);
	}

	public Mirror<T> mirror() {
		return mirror;
	}

	@SuppressWarnings("unchecked")
	public <C extends T> void setEntityType(Class<C> classOfT) {
		mirror = (Mirror<T>) Mirror.me(classOfT);
	}

	public Entity<T> getEntity() {
		return dao().getEntity(mirror.getType());
	}

	public Class<T> getEntityClass() {
		return mirror.getType();
	}

	public void clear(Condition condition) {
		dao().clear(getEntityClass(), condition);
	}

	public T insert(T obj) {
		checkObjectType(obj);
		return dao().insert(obj);
	}

	private void checkObjectType(T obj) {
		if (null != obj)
			if (obj.getClass() != mirror.getType())
				throw Lang.makeThrow(
						"Current service is for type '%s', but you object type is '%s'", mirror
								.getType().getName(), obj.getClass());
	}

	public void delete(T obj) {
		checkObjectType(obj);
		dao().delete(obj);
	}

	public List<T> query(Condition condition, Pager pager) {
		return (List<T>) dao().query(getEntityClass(), condition, pager);
	}

	public T update(T obj) {
		checkObjectType(obj);
		return dao().update(obj);
	}

	public int count(Condition condition) {
		return dao().count(getEntityClass(), condition);
	}

	public int count() {
		return dao().count(getEntityClass());
	}

}
