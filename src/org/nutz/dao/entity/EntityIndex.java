package org.nutz.dao.entity;

import java.util.List;

/**
 * 封装了实体的索引
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface EntityIndex {

    /**
     * @return 是否是唯一性索引
     */
    boolean isUnique();

    /**
     * @return 索引名称
     */
    String getName();

    /**
     * @return 按顺序的索引实体字段
     */
    List<EntityField> getFields();

}
