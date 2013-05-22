package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个 Java 字段与数据库字段的便捷对应方式
 * <p>
 * 如果 Java 字段名与数据库字段名是按照某种规则来一一对应，<br>
 * 比如 Java 字段命名方式为驼峰式大小写（ camelCase ），而数据库字段为蛇底式小写（ snake_case ），<br>
 * 通过这个注解可以快速的设置对应关系:
 * 
 * <pre>
 *  &#064;SimpleColumn
 * </pre>
 * 
 * 该注解默认使用底线（ _ ）连结。<br>
 * 如果数据库字段为连接号（ - ）或其他字符来连接的话，则把该字符当成参数传入。
 * 
 * <pre>
 *  &#064;SimpleColumn(&#x27;-&#x27;)
 * </pre>
 * 
 * <b style=color:red>需要说明的是：</b>
 * <ul>
 * <li>声明了 '@Column' 的话，则按照 '@Column' 注解的效果来对该 POJO 进行处理。
 * <li>对于该注解来说，在 POJO 对象的属性的声明优先于在 POJO 上的声明。
 * <li>该注解能用在声明了 '@Id' 和 '@Name' 的字段上。
 * </ul>
 * 
 * @author ywjno(ywjno.dev@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface SimpleColumn {
    char value() default '_';
}
