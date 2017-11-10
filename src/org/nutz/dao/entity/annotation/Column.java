package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个 Java 字段是否有对应的数据库字段
 * <p>
 * 如果数据库字段名同 JAVA 字段名不一样，通过这个注解可以进行特殊标注:
 *
 * <pre>
 * &#064;Column(&quot;数据库字段名&quot;)
 * </pre>
 *
 * <b style=color:red>需要说明的是：</b>
 * <ul>
 * <li>如果你的 POJO <u><b>没有任何一个字段</b></u> 声明了这个注解，那么所有的 Java 字段都会被认为是数据库字段。
 * <li>声明了 '@Id' 和 '@Name' 的字段没必要声明这个注解。
 * </ul>
 *
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Column {

    String value() default "";

    /**
     * 是否把字段的命名方式从驼峰式大小写(camelCase)变成蛇底式小写(snake_case)
     *
     * @return true: 蛇底式小写，false: 驼峰式大小写
     */
    boolean hump() default false;

    boolean wrap() default false;

    boolean version() default false;

    /** 字段前缀 */
    String prefix() default "";

    /** 字段后缀 */
    String suffix() default "";
}
