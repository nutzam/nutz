package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一组过滤器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.mvc.annotation.By
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Filters {

    By[] value() default {};

}
