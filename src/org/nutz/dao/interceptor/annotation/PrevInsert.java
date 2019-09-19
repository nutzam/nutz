package org.nutz.dao.interceptor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.dao.entity.annotation.EL;

/**
 * 在执行插入操作时触发
 * @author wendal
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface PrevInsert {

    /**
     * 执行一个EL表达式,如果返回值不是null,赋值到当前字段
     */
    EL[] els() default {};
    
    /**
     * 设置为当前时间,通常是createTime字段
     */
    boolean now() default false;
    
    /**
     * 设置为UUID, 为nutz定义的UU32格式,通常配合@Name使用
     */
    boolean uu32() default false;

    /**
     * nullEffective=true时上面的赋值规则要起效必须是在[当前字段==null]时才能生效
     */
    boolean nullEffective() default false;
}
