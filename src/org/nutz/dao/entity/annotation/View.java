package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个实体，应该从何处获取。默认的，会从 '@Table' 注解声明的表名获取。
 * <p>
 * 但是，某些时候，为了获得一些统计信息，你可能需要创建一个视图，而希望从视图获取自己的对象。
 * <p>
 * 那么在你的类上声明本注解，就可以做到这一点
 * <p>
 * 和注解 '@Table' 一样，注解的值可以支持动态表名
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.Table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface View {
    String value();
}
