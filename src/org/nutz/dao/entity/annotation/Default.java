package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为一个字段声明默认值。 Nutz.Dao 在发现一个字段没有被设值时，会用你声明的这个 默认值填出字段，再执行操作。
 * <p>
 * 默认值可以是字符串模板:
 * <p>
 * 可以支持书写类似 '@Default("${name}@gmail.com")' 这样的语法， 其中类似 ${XXXXX}
 * 的占位符，会被本对象相应字段的值在运行时替换
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Default {

    String value();

}
