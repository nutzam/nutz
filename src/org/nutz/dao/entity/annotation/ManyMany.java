package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 本注解声明了多对多的关联，它需要 5 个参数，其中一个是可选的：
 * <ul>
 * <li><b>target:</b> 对应的实体类名，意义和@One 和 @Many 一样
 * <li><b>relation:</b> 中间的关联表的名称，这个关联表也可以动态的，详细请参看 '@Table' 的描述
 * <li><b>from:</b> 表示关联表中哪个字段代表主对象
 * <li><b>to:</b> 表示关联表中哪个字段代表 target 对象
 * <li><b>key:</b> <i>[可选]</i> 同 '@Many' 中的同名参数意义一样。
 * </ul>
 * 
 * <b style=color:red>你还需要知道： </b><br>
 * 两个对象的关联是通过 Id 或者 Name 来关联的，关联的优先级为
 * <ol>
 * <li>@Id <-> @Id
 * <li>@Id <-> @Name
 * <li>@Name <-> @Id
 * <li>@Name <-> @Name
 * </ol>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.One
 * @see org.nutz.dao.entity.annotation.Many
 * @see org.nutz.dao.entity.annotation.Table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface ManyMany {

	/**
	 * 关联类
	 */
    Class<?> target();

    /**
     * 中间表的名称
     */
    String relation();

    /**
     * 关联表中哪个字段代表主对象
     */
    String from();

    /**
     * 关联表中哪个字段代表 target 对象
     */
    String to();

    /**
     * 指定关联类的一个属性名,缺省情况下,按参考字段名{@link #field()}的类型选取@Id或者@Name等主键字段
     */
    String key() default "";

}
