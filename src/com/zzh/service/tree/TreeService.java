package com.zzh.service.tree;

import java.util.LinkedList;
import java.util.List;

import com.zzh.dao.Dao;
import com.zzh.lang.Each;
import com.zzh.lang.ExitLoop;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.service.IdEntityService;
import com.zzh.trans.Atom;
import com.zzh.trans.Trans;

public abstract class TreeService<T> extends IdEntityService<T> {

	protected TreeService() {
		super();
	}

	protected TreeService(Dao dao) {
		super(dao);
	}

	private String parentField;
	private String childrenField;

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String parent) {
		this.parentField = parent;
	}

	public String getChildrenField() {
		return childrenField;
	}

	public void setChildrenField(String children) {
		this.childrenField = children;
	}

	public void clearDescendants(final T obj) {
		Trans.exec(new Atom() {
			public void run() throws Exception {
				dao().fetchMany(obj, childrenField);
				Object children = Mirror.me(obj.getClass()).getValue(obj, childrenField);
				Lang.each(children, new Each<T>() {
					public void invoke(int index, T child, int size) throws ExitLoop {
						clearDescendants(child);
						dao().delete(child);
					}
				});
			}
		});
	}

	public void clearDescendants(long id) {
		clearDescendants(fetch(id));
	}

	public void clearChildren(T obj) {
		dao().clearMany(obj, childrenField);
	}

	public void clearChildren(long id) {
		clearChildren(fetch(id));
	}

	public T fetchWithDescendants(T obj) {
		if (null == obj)
			return null;
		dao().fetchMany(obj, childrenField);
		Object children = Mirror.me(obj.getClass()).getValue(obj, childrenField);
		Lang.each(children, new Each<T>() {
			public void invoke(int index, T child, int size) throws ExitLoop {
				fetchWithDescendants(child);
			}
		});
		return obj;
	}

	public T fetchWithDescendants(long id) {
		return fetchWithDescendants(fetch(id));
	}

	public T fetchWithChildren(long id) {
		return dao().fetchMany(fetch(id), childrenField);
	}

	public T fetchWithParent(long id) {
		return dao().fetchOne(fetch(id), parentField);
	}

	public T fetchWithAncestors(long id) {
		return fetchAncestors(fetch(id));
	}

	public T fetchChildren(T obj) {
		return dao().fetchMany(obj, childrenField);
	}

	public T fetchParent(T obj) {
		return dao().fetchOne(obj, parentField);
	}

	public T fetchAncestors(T obj) {
		dao().fetchOne(obj, parentField);
		T parent = evalParent(obj);
		if (null != parent)
			fetchAncestors(parent);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private T evalParent(T obj) {
		T parent = (T) Mirror.me(obj.getClass()).getValue(obj, parentField);
		return parent;
	}

	public List<T> getAncestors(T obj) {
		List<T> list = new LinkedList<T>();
		while (null != obj) {
			fetchParent(obj);
			T parent = evalParent(obj);
			if (null != parent)
				list.add(0, parent);
			obj = parent;
		}
		return list;
	}

	public List<T> getAncestor(long id) {
		return getAncestors(fetch(id));
	}

}
