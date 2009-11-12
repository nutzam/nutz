package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注解
 * <p>
 * 标识当前字段为一个 ID。 这字段的必须为整型（int,long,short,byte），否则 会在解析 POJO 时主动抛出异常。
 * <p>
 * 在 Dao 接口调用 xxxx(Class<?>, long) 形式的函数时，第二个参数对应的就是这个字段，比如： <br>
 * fetch(Class<?>,long)
 * 
 * <h4>自动增长 -- 默认模式</h4>
 * <p>
 * 默认的来说，这个字段在数据库中必须是自动增长的。当通过 Dao 接口执行 insert 操作<br>
 * 的时候，这个字段会被 自动填充上增长后的值。
 * <p>
 * 不同的数据库，获得自动增长值的语法都是不一样的。比如 Postgresql 或者 Oracle 是<br>
 * 用序列+触发器 的方式实现自动增长字段。你可以在这个注解中写一段 SQL，Nutz.Dao 会用这段<br>
 * SQL 来获取自动增长的值。比如在 Postgresql 中，你一般会这样写:
 * 
 * <pre>
 * &#064;Id(&quot;SELECT currval('my_id_seq')&quot;)
 * private int id;
 * </pre>
 * 
 * 如果你想让你的 POJO 同时兼容不同种的数据库，你可以
 * 
 * <pre>
 * &#064;Id({&quot;psql&quot;, &quot;SELECT currval('my_id_seq')&quot;, &quot;mysql&quot;, &quot;SELECT max(id) FROM my_table&quot;})
 * private int id;
 * </pre>
 * 
 * 偶数字符串数组，偶数下标和0 为数据库的名称，奇数位，为取值的 SQL。<br>
 * 截至到现在，数据库名称可以支持下列字符串:
 * 
 * <ul>
 * <li>db2
 * <li>psql
 * <li>oracle
 * <li>sqlserver
 * <li>mysql
 * </ul>
 * 
 * <h4>手动模式</h4>
 * 有些时候，你不希望这个 ID 是由你的程序来控制，你可以将 auto 属性设为 false
 * 
 * <pre>
 * &#064;Id(auto = false)
 * private int id;
 * </pre>
 * 
 * 这样，插入的时候，就不会从数据库中更新自增长值了
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.Dao
 * @see org.nutz.dao.DatabaseMeta
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Id {
	/**
	 * true : auto increasement
	 */
	boolean auto() default true;

	/**
	 * How to get the new auto increasement value.
	 */
	String[] value() default {};
}
