package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;

/**
 * 声明一个数据表的索引
 * 
 * @see TableIndexes
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Documented
public @interface Index {

    /**
     * 是否是唯一性索引
     */
    boolean unique() default true;

    /**
     * 索引的名称
     */
    String name() default "";

    /**
     * 按顺序给出索引的字段名（推荐，用 Java 的字段名）
     */
    String[] fields();

}
