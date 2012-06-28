package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个字段为 Name
 * <p>
 * Name 字段，即字符型主键。如果你将这个注解声明在非 Charsequence 类型的字段上，会抛出异常
 * <p>
 * 属性 casesensitive 将指明，这个主键是否为大小写敏感
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Name {
    boolean casesensitive() default true;
}
