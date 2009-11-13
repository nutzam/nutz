package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动生成字段的值。
 * <p>
 * 某些时候，字段的值希望交给数据库来控制。在相应的字段声明本注解可以达到这个目的。
 * <p>
 * 注解的赋值方式，请参看 '@Id' 的相关描述
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Next {

	String[] value();

}
