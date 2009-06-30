package org.nutz.service.tree;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.service.IdEntityService;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public abstract class TreeService<T> extends IdEntityService<T> {

	protected TreeService() {
		super();
		resetDefaultFields();
	}

	private void resetDefaultFields() {
		try {
			this.setChildrenField("children");
		} catch (NoSuchFieldException e) {}
		try {
			this.setParentField("parent");
		} catch (NoSuchFieldException e) {}
	}

	protected TreeService(Dao dao) {
		super(dao);
		resetDefaultFields();
	}

	private Field parentField;
	private Field childrenField;

	public Field getParentField() {
		return parentField;
	}

	public void setParentField(String fieldName) throws NoSuchFieldException {
		this.parentField = mirror().getField(fieldName);
	}

	public Field getChildrenField() {
		return childrenField;
	}

	public void setChildrenField(String fieldName) throws NoSuchFieldException {
		this.childrenField = mirror().getField(fieldName);
	}

	public void clearDescendants(final T obj) {
		Trans.exec(new Atom() {
			public void run() {
				if (null == dao().fetchLinks(obj, childrenField.getName()))
					return;
				Object children = Mirror.me(obj.getClass()).getValue(obj, childrenField.getName());
				Lang.each(children, new Each<T>() {
					public void invoke(int index, T child, int size) throws ExitLoop {
						clearDescendants(child);
						dao().delete(child);
					}
				});
			}
		});
	}

	public void clearChildren(T obj) {
		dao().deleteLinks(obj, childrenField.getName());
	}

	public T fetchDescendants(T obj) {
		if (null == dao().fetchLinks(obj, childrenField.getName()))
			return obj;
		Mirror<?> mirror = Mirror.me(obj.getClass());
		Object children = mirror.getValue(obj, childrenField.getName());
		Lang.each(children, new Each<T>() {
			public void invoke(int index, T child, int size) throws ExitLoop {
				fetchDescendants(child);
			}
		});
		return obj;
	}

	public T fetchAll(T obj) {
		if (null == dao().fetchLinks(obj, childrenField.getName()))
			return obj;
		Mirror<?> mirror = Mirror.me(obj.getClass());
		Object children = setupParentForChildren(obj, mirror, childrenField, parentField);
		Lang.each(children, new Each<T>() {
			public void invoke(int index, T child, int size) throws ExitLoop {
				fetchAll(child);
			}
		});
		return obj;
	}

	private static <T> Object setupParentForChildren(final T me, final Mirror<?> mirror,
			final Field childrenField, final Field parentField) {
		Object children = mirror.getValue(me, childrenField.getName());
		Lang.each(children, new Each<T>() {
			public void invoke(int index, T child, int size) throws ExitLoop {
				mirror.setValue(child, parentField, me);
			}
		});
		return children;
	}

	public T fetchChildren(T obj) {
		if (null == fetchChildrenOnly(obj))
			return obj;
		setupParentForChildren(obj, Mirror.me(obj.getClass()), childrenField, parentField);
		return obj;
	}

	public T fetchChildrenOnly(T obj) {
		return dao().fetchLinks(obj, childrenField.getName());
	}

	public T fetchParent(T obj) {
		return dao().fetchLinks(obj, parentField.getName());
	}

	public T fetchAncestors(T obj) {
		if (null == dao().fetchLinks(obj, parentField.getName()))
			return obj;
		T parent = evalParent(obj);
		if (null != parent)
			fetchAncestors(parent);
		return obj;
	}

	public T insertChildren(T obj) {
		if (null == obj)
			return null;
		Entity<?> entity = dao().getEntity(obj.getClass());
		Mirror<?> mirror = entity.getMirror();
		Object children = mirror.getValue(obj, childrenField.getName());
		Link link = Lang.first(entity.getLinks(childrenField.getName()));
		Field pIdField = link.getTargetField();
		this.insertBy(obj, children, entity, mirror, pIdField, new InsertByCallback<T>() {
			public void processInsert(T child) {
				dao().insert(child);
			}
		});
		return obj;
	}

	private static interface InsertByCallback<T> {
		void processInsert(T child);
	}

	private void insertBy(T obj, final Object children, Entity<?> entity, final Mirror<?> mirror,
			final Field pIdField, final InsertByCallback<T> callback) {
		if (Lang.lenght(children) > 0) {
			final Object id = mirror.getValue(obj, entity.getIdField().getField());
			Trans.exec(new Atom() {
				public void run() {
					Lang.each(children, new Each<T>() {
						public void invoke(int i, T child, int length) throws ExitLoop {
							mirror.setValue(child, pIdField, id);
							callback.processInsert(child);
						}
					});
				}
			});
		}
	}

	public T insertDescendants(T obj) {
		if (null == obj)
			return null;
		final Entity<?> entity = dao().getEntity(obj.getClass());
		final Mirror<?> mirror = entity.getMirror();
		Object children = mirror.getValue(obj, childrenField.getName());
		Link link = Lang.first(entity.getLinks(childrenField.getName()));
		final Field pIdField = link.getTargetField();
		InsertByCallback<T> callback = new InsertByCallback<T>() {
			public void processInsert(T child) {
				Object children = mirror.getValue(child, childrenField.getName());
				dao().insert(child);
				insertBy(child, children, entity, mirror, pIdField, this);
			}
		};
		insertBy(obj, children, entity, mirror, pIdField, callback);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private T evalParent(T obj) {
		return (T) Mirror.me(obj.getClass()).getValue(obj, parentField);
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

}
