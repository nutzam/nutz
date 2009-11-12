package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.dao.entity.FieldValueType;

/**
 * 某些字段在数据库中的类型，很难通过其 Java 类型类推断，比如枚举。
 * <p>
 * 这个注解可以更显示的指明。
 * <p>
 * 枚举类型，如果不做特殊声明，其数据库字段类型将被认为是 VCHAR
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ValueType {

	FieldValueType value() default FieldValueType.AUTO;
}
