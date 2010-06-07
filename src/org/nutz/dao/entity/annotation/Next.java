package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当插入一个对象之后，通过一个 SQL 为字段设置值。
 * <p>
 * 比如一个自增的字段（数据库通过触发器和序列为其自动+1），插入后，可以通过一条 SQL 获取这个字段的值
 * <p>
 * <b style=color:red>注意：</b> 你的字段只能允许是字符型的，或者是整数型的。否则会报错。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.SQL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Next {

	SQL[] value();

}
