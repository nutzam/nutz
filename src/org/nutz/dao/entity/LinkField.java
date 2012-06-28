package org.nutz.dao.entity;

import org.nutz.dao.Condition;
import org.nutz.dao.sql.PojoCallback;

/**
 * 这个接口封装了不同映射关系行为的不同
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface LinkField extends EntityField {

    /**
     * @return 映射的类型
     */
    LinkType getLinkType();

    /**
     * @return 对应的宿主字段
     */
    MappingField getHostField();

    /**
     * @return 对应的映射实体的被映射字段
     */
    MappingField getLinkedField();

    /**
     * 根据给定的宿主对象，以及自身记录的映射关系，生成一个获取映射对象的约束条件
     * <ul>
     * <li>`@One` 根据宿主对象引用字段值生成映射对象的条件语句</li>
     * <li>`@Many` 根据宿主对象主键值生成映射对象的条件语句</li>
     * <li>`@ManyMany` 根据宿主对象以及映射表生成映射对象的条件语句</li>
     * </ul>
     * 
     * @param host
     *            宿主对象
     * 
     * @return POJO 语句的条件元素
     */
    Condition createCondition(Object host);

    /**
     * @return 执行查询的回调
     */
    PojoCallback getCallback();

    /**
     * 用宿主对象的字段更新映射对象
     * 
     * @param obj
     *            宿主对象
     * @param linked
     *            被映射的对象
     */
    void updateLinkedField(Object obj, Object linked);

    /**
     * 用映射对象的字段更新宿主对象
     * 
     * @param obj
     *            宿主对象
     * @param linked
     *            被映射的对象
     */
    void saveLinkedField(Object obj, Object linked);

    /**
     * @return 链接的目标实体
     */
    Entity<?> getLinkedEntity();

    /**
     * @return 打印映射信息
     */
    String toString();

}
