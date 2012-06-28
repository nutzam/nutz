package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过一个 Json 字符串，为这个数据对象进行更详细的设置。
 * <p>
 * 这个字符串会被变成 Map&lt;String,String&gt;，你可以在运行时随时取到。 <br>
 * 尤其时是对某些 JdbcExpert，这个 Map 会有特殊含义<br>
 * 比如 MysqlExpert 会根据这个 Map 改变一个对象默认的数据库引擎
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface TableMeta {

    String value();

}
