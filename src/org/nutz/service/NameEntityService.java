package org.nutz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.EntityField;

/**
 * 针对标注了@Name的实体的Service
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> 实体的类型
 */
public abstract class NameEntityService<T> extends EntityService<T> {

    /**
     * @see EntityService
     */
    public NameEntityService() {
        super();
    }

    /**
     * @see EntityService
     */
    public NameEntityService(Dao dao) {
        super(dao);
    }

    /**
     * @see EntityService
     */
    public NameEntityService(Dao dao, Class<T> entityType) {
        super(dao, entityType);
    }

    /**
     * 根据@Name所在的属性的值删除一条记录
     * @param name 属性的值
     * @return 删除的记录数,通常是0或者1
     */
    public int delete(String name) {
        return dao().delete(getEntityClass(), name);
    }
    
    /**
     * 根据@Name所在的属性的值获取一个实体对象
     * @param name 属性的值
     * @return 实体对象,若没有符合条件的记录,则返回null
     */
    public T fetch(String name) {
        return dao().fetch(getEntityClass(), name);
    }

    /**
     * 是否存在@Name所在的属性与指定值相符的记录
     * @param name 属性的值
     * @return true,如果存在符合条件的记录
     */
    public boolean exists(String name) {
        EntityField ef = getEntity().getNameField();
        if (null == ef)
            return false;
        return dao().count(getEntityClass(), Cnd.where(ef.getName(), "=", name)) > 0;
    }

}
