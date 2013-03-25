package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在一个字段上声明一条一对一映射，这个声明需要你输入两个参数:
 * <ul>
 * <li><b>target</b>: 你的这个字段对应的实体类。通常，这个类得是你的字段的一个子类或者实现类。或者它能够顺利的通过 Nutz.castor
 * 转换成 你的字段
 * <li><b>field</b>: 参考字段名，这个字段为对应对象的 Java 字段名称。
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface One {

	/**
	 * 关联类
	 */
    Class<?> target();

    /**
     * 关联属性名
     */
    String field();
    
    /**
     * 指定关联类的一个属性名,缺省情况下,按参考字段名{@link #field()}的类型选取@Id或者@Name等主键字段
     */
    String key() default "";

}
