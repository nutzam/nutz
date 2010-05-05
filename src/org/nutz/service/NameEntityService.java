package org.nutz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.EntityField;

public abstract class NameEntityService<T> extends EntityService<T> {

	protected NameEntityService() {
		super();
	}

	protected NameEntityService(Dao dao) {
		super(dao);
	}

	public int delete(String name) {
		return dao().delete(getEntityClass(), name);
	}

	public T fetch(String name) {
		return dao().fetch(getEntityClass(), name);
	}

	public boolean exists(String name) {
		EntityField ef = getEntity().getNameField();
		if (null == ef)
			return false;
		return dao().count(getEntityClass(), Cnd.where(ef.getName(), "=", name)) > 0;
	}

}
