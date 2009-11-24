package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当插入一个对象是，自动生成字段的值。
 * <p>
 * 某些时候，字段的值希望交给数据库来控制。在相应的字段声明本注解可以达到这个目的。
 * <p>
 * 你可以为某一个字段声明一段 SQL，Nutz.Dao 在执行插入操作的时候，会用这段 SQL 为你的字段 赋值。
 * <p>
 * <b style=color:red>注意：</b> 你的字段只能允许是字符型的，或者是整数型的。否则会报错。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.SQL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Next {

	SQL[] value();

}
