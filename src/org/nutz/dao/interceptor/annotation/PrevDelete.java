package org.nutz.dao.interceptor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.dao.entity.annotation.EL;

/**
 * 在执行删除操作时触发
 * @author wendal
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface PrevDelete {

    /**
     * 执行一个EL表达式,如果返回值不是null,赋值到当前字段
     */
    EL[] els() default {};
}
