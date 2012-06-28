package org.nutz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.EntityField;

public abstract class IdNameEntityService<T> extends IdEntityService<T> {

    public IdNameEntityService() {
        super();
    }

    public IdNameEntityService(Dao dao) {
        super(dao);
    }

    public IdNameEntityService(Dao dao, Class<T> entityType) {
        super(dao, entityType);
    }

    public int delete(String name) {
        return dao().delete(getEntityClass(), name);
    }

    public T fetch(String name) {
        return dao().fetch(getEntityClass(), name);
    }

    public T smartFetch(String str) {
        try {
            long id = Long.parseLong(str);
            return fetch(id);
        }
        catch (Exception e) {}
        return fetch(str);
    }

    public boolean exists(String name) {
        EntityField ef = getEntity().getNameField();
        if (null == ef)
            return false;
        return dao().count(getEntityClass(), Cnd.where(ef.getName(), "=", name)) > 0;
    }
}
