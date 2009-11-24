package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为一个字段声明默认值。 Nutz.Dao 在发现一个字段没有被设值时，会用你声明的这个 默认值填出字段，再执行操作。
 * <p>
 * 默认值可以是两种形式：
 * <ul>
 * <li>字符串模板: 可以支持书写类似 '@Default("${name @ gmail.com")' 这样的语法， 其中类似 ${XXXXX}
 * 的占位符，会被本对象相应字段的值运行时替换
 * <li>SQL 语句： 请参看 '@SQL' 注解的说明
 * </ul>
 * <b style=color:red>注意:</b> SQL 将会
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.SQL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Default {

	String as() default "";

	SQL[] value() default {};
}
