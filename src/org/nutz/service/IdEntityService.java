package org.nutz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.EntityField;

/**
 * 针对仅标注了@Id的实体的Service
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> 实体的类型
 */
public abstract class IdEntityService<T> extends EntityService<T> {

    /**
     * @see EntityService
     */
    public IdEntityService() {
        super();
    }

    /**
     * @see EntityService
     */
    public IdEntityService(Dao dao) {
        super(dao);
    }

    /**
     * @see EntityService
     */
    public IdEntityService(Dao dao, Class<T> entityType) {
        super(dao, entityType);
    }

    /**
     * 根据@Id所在的属性的值获取一个实体对象
     * @param id 属性的值
     * @return 实体对象,如不存在则返回null
     */
    public T fetch(long id) {
        return dao().fetch(getEntityClass(), id);
    }

    /**
     * 根据@Id所在的属性的值删除一个实体对象
     * @param id 属性的值
     * @return 删除的记录数, 通常是0或者1
     */
    public int delete(long id) {
        return dao().delete(getEntityClass(), id);
    }

    /**
     * 根据@Id所在的属性在数据库中的最大值
     * @return 最大值,若数据库中没有数据,会抛出空指针异常
     */
    public int getMaxId() {
        return dao().getMaxId(getEntityClass());
    }

    /**
     * 是否存在@Id所在属性的值为指定值的记录
     * @param id 属性的值
     * @return true,如果存在的话
     */
    public boolean exists(long id) {
        EntityField ef = getEntity().getIdField();
        if (null == ef)
            return false;
        return dao().count(getEntityClass(), Cnd.where(ef.getName(), "=", id)) > 0;
    }

}
