package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识当前字段为一个 ID。 这字段的必须为整型（int,long,short,byte），否则 会在解析 POJO 时主动抛出异常。
 * <p>
 * 在 Dao 接口调用 xxxx(Class<?>, long) 形式的函数时，第二个参数对应的就是这个字段，比如： <br>
 * fetch(Class<?>,long)
 * 
 * <h4>自动增长 -- 默认模式</h4>
 * <p>
 * 默认的来说，这个字段在数据库中必须是自动增长的。当通过 Dao 接口执行 insert 操作 的时候，这个字段会被 自动填充上增长后的值。
 * <p>
 * 在自增长模式下， Nutz.Dao 在执行插入的时候，会忽略这个字段。如果你想在插入后获取数据库中的值，请 使用 '@Next' 注解
 * 
 * <h4>手动模式</h4>
 * 有些时候，你希望这个 ID 的值是由你的程序来控制，你可以将 auto 属性设为 false
 * 
 * <pre>
 * &#064;Id(auto = false)
 * private int id;
 * </pre>
 * 
 * 这样，插入的时候，Nutz.Dao 就不会忽略这个字段了。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.entity.annotation.Next
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Id {
    /**
     * true : auto increasement
     */
    boolean auto() default true;

}
