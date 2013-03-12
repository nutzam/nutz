package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一条一对多映射，这个声明需要你输入三个参数，其中一个是可选的:
 * <ul>
 * <li><b>target</b>: 你的这个字段对应的实体类。通常，这个类得是你的字段的一个子类或者实现类。或者它能够顺利的通过 Nutz.castor
 * 转换成 你的字段
 * <li><b>field</b>: 参考字段(<i>或者说是"关联字段"</i>)名，同 '@One' 不同，这个参考字段是 target 类中的字段。如果它为空串，则将映射目标实体全部记录
 * <li><b>key</b>: <i>[可选]</i> 如果本注解声明在一个 Map 字段上，这个参数指明了你的 POJO 哪个字段可以作为 key
 * </ul>
 * 
 * <h4 style=color:red>Can be Many:</h4>
 * <p>
 * <blockquote> 本注解是一个略微让人疑惑的名称，你可能会想，你声明 '@Many' 的字段必须是一个容器或者数组，其实，它也可以
 * 是一个单个对象。
 * <p>
 * 对于 Nutz.Dao来说，'@Many' 确切的意义是说： <b>Can be Many</b> <br>
 * 所以,你可以当然这么写：
 * 
 * <pre>
 * &#064;Many(target = Pet.class, field = &quot;id&quot;)
 * private Pet pet;
 * </pre>
 * 
 * </blockquote>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Many {

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
