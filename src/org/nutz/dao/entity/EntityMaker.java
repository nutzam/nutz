package org.nutz.dao.entity;

import javax.sql.DataSource;

import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.jdbc.JdbcExpert;

/**
 * Entity 的工厂接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 *            Entity 的配置对象类型
 */
public interface EntityMaker {

    /**
     * 根据一个配置信息，生成一个新的 Entity 的实例
     * 
     * @param type
     *            Entity 的配置信息
     * @return Entity 实例
     */
    <T> Entity<T> make(Class<T> type);

    void init(DataSource datasource, JdbcExpert expert, EntityHolder holder);
}
