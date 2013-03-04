package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明了一个 POJO 的主键。
 * <p>
 * 本注解声明在某一个 POJO 类上，例如：
 * 
 * <pre>
 * &#064;Table("t_abc")
 * &#064;PK({"id", "type"})
 * public class Abc{
 * ...
 * </pre>
 * 
 * 其中， "id" 和 "type" 必须是这个 POJO 的 Java 字段名
 * 
 * <p>
 * 这个注解主要应用在复合主键的情况，如果一个 POJO 是复合主键的话，你需要通过
 * <ul>
 * <li>fetchx(Class<?>,Object ...) 来获取一个对象
 * <li>deletex(Class<?>,Object ...) 来删除一个对象
 * </ul>
 * 变参给入的顺序，需要按照本注解声明的顺序，否则会发生不可预知的错误。
 * <p>
 * 当然，你可以通过这个注解来替代 '@Id' 和 '@Name'，当你给出的字段只有一个的时候
 * <ul>
 * <li>整数型字段，将代表 '@Id'
 * <li>字符型字段，将代表 '@Name'
 * </ul>
 * 在 POJO 中，你可以同时声明 '@Id'，'@Name'以及 '@Pk'，但是 '@Id' 和 '@Name' 更优先
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.Dao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface PK {

    String[] value();
    
    /**
     * 仅建表时使用
     */
    String name() default "";

}
