package org.nutz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.EntityField;

public abstract class IdEntityService<T> extends EntityService<T> {

    public IdEntityService() {
        super();
    }

    public IdEntityService(Dao dao) {
        super(dao);
    }

    public IdEntityService(Dao dao, Class<T> entityType) {
        super(dao, entityType);
    }

    public T fetch(long id) {
        return dao().fetch(getEntityClass(), id);
    }

    public int delete(long id) {
        return dao().delete(getEntityClass(), id);
    }

    public int getMaxId() {
        return dao().getMaxId(getEntityClass());
    }

    public boolean exists(long id) {
        EntityField ef = getEntity().getIdField();
        if (null == ef)
            return false;
        return dao().count(getEntityClass(), Cnd.where(ef.getName(), "=", id)) > 0;
    }

}
