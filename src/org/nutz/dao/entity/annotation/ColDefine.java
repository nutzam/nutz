package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * 给出字段的更加精确的数据库类型描述，方便 Dao 创建数据表
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface ColDefine {

	/**
	 * 数据库字段类型
	 * 
	 * @see org.nutz.dao.entity.annotation.ColType
	 */
	ColType type() default ColType.VARCHAR;

	/**
	 * 宽度/长度, 例如定义字符串长度为1024 就写  width=1024
	 */
	int width() default 0;

	/**
	 * 精度,小数点后多少位,默认是2
	 */
	int precision() default 2;

	/**
	 * 是否为非空,默认为false
	 */
	boolean notNull() default false;

	/**
	 * 是否为无符号数值,默认为false
	 */
	boolean unsigned() default false;

	/**
	 * 描述当前字段是否自增，如果和 @Id 冲突，以 @Id 的优先
	 */
	boolean auto() default false;

	/**
	 * 自定义数据库字段类型, 例如写  customType="image" 等, 然后<b>请务必再设置type属性!!</b>
	 * @return
	 */
	String customType() default "";

	/**
	 * 描述当前字段是否可插入
	 */
	boolean insert() default true;

	/**
	 * 描述当前字段是否可更新
	 */
	boolean update() default true;
	
	/**
	 * 自定义ValueAdaptor
	 */

	Class<? extends ValueAdaptor> adaptor() default ValueAdaptor.class;
}
