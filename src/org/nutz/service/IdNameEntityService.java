package org.nutz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.EntityField;

/**
 * 针对标注了@Id和@的实体类的Service
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> 实体类的类型
 */
public abstract class IdNameEntityService<T> extends IdEntityService<T> {

    /**
     * @see IdEntityService
     * @see NameEntityService
     */
    public IdNameEntityService() {
        super();
    }

    /**
     * @see IdEntityService
     * @see NameEntityService
     */
    public IdNameEntityService(Dao dao) {
        super(dao);
    }

    /**
     * @see IdEntityService
     * @see NameEntityService
     */
    public IdNameEntityService(Dao dao, Class<T> entityType) {
        super(dao, entityType);
    }

    /**
     * 根据@Name所标注的属性的值进行删除
     * @param name 属性的值
     * @return 删除的记录数,通常为1或0
     */
    public int delete(String name) {
        return dao().delete(getEntityClass(), name);
    }

    /**
     * 根据@Name所标注的属性的值,获取一个实体
     * @param name 属性的值
     * @return 实体,如不存在则返回null
     */
    public T fetch(String name) {
        return dao().fetch(getEntityClass(), name);
    }

    /**
     * 智能获取一个实体,如str能转为Long类型,则调用fetch(long id),否则调用fetch(String name)
     * @param str 非空的字符串,可以是整数或普通字符串
     * @return 符合条件的记录,如不存在则返回null
     */
    public T smartFetch(String str) {
        try {
            long id = Long.parseLong(str);
            return fetch(id);
        }
        catch (Exception e) {}
        return fetch(str);
    }

    /**
     * 是否存在符合条件的记录,需要实体有@Name标注
     * @param name 属性的值
     * @return true,如果存在的话.
     */
    public boolean exists(String name) {
        EntityField ef = getEntity().getNameField();
        if (null == ef)
            return false;
        return dao().count(getEntityClass(), Cnd.where(ef.getName(), "=", name)) > 0;
    }
}
