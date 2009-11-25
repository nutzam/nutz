package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当插入一个对象之前，通过一个 SQL 为字段设置值。
 * <p>
 * <b style=color:red>注意：</b> 你的字段只能允许是字符型的，或者是整数型的。否则会报错。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.SQL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Prev {

	SQL[] value();
}
