package com.zzh.service.tree;

import java.util.List;

import com.zzh.dao.Dao;

public class NameTreeService<T> extends TreeService<T> {

	protected NameTreeService() {
		super();
	}

	protected NameTreeService(Dao dao) {
		super(dao);
	}

	public void delete(String name) {
		dao().delete(getEntityClass(), name);
	}

	public void clearDescendants(String name) {
		clearDescendants(fetch(name));
	}

	public void clearChildren(String name) {
		clearChildren(fetch(name));
	}

	public T fetch(String name) {
		return dao().fetch(getEntityClass(), name);
	}

	public T fetchWithDescendants(String name) {
		return fetchWithDescendants(fetch(name));
	}

	public T fetchWithChildren(String name) {
		return dao().fetchMany(fetch(name), getChildrenField());
	}

	public T fetchWithParent(String name) {
		return dao().fetchOne(fetch(name), getChildrenField());
	}

	public T fetchWithAncestors(String name) {
		return fetchAncestors(fetch(name));
	}

	public List<T> getAncestors(String name) {
		return getAncestors(fetch(name));
	}
}
