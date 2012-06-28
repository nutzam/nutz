package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.dao.DB;

/**
 * 为 '@Next' 以及 '@Prev' 注解声明的可执行 SQL。
 * <p>
 * 支持两个属性：
 * <p>
 * <b>db</b><br>
 * 表示本条 SQL 可以应用到的数据库。DatabaseType.UNKNOWN 为默认值，表示 适用于任何数据库。
 * 
 * <p>
 * <b>value</b><br>
 * 声明了一条 SQL 语句，支持动态占位符。
 * <ul>
 * <li>变量: $XXX ，由 org.nutz.dao.TableName 来设置，以支持动态表名
 * <ul>
 * <li>特殊占位符: $view: 表示当前实体对象的视图名称
 * <li>特殊占位符: $field: 表示注解所在字段数据库名称
 * </ul>
 * <li>参数: @XXX， 直接参考对象自身的属性值
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.DB
 * @see org.nutz.dao.TableName
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface SQL {

    DB db() default DB.OTHER;

    String value();

}
