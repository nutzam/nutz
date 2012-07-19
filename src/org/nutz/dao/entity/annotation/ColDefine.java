package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 给出字段的更加精确的数据库类型描述，方便 Dao 创建数据表
 * 
 * @author zozoh(zozohtnt@gmail.com)
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

	int width() default 0;

	int precision() default 2;

	boolean notNull() default false;

	boolean unsigned() default false;

	/**
	 * 描述当前字段是否自增，如果和 @Id 冲突，以 @Id 的优先
	 */
	boolean auto() default false;

	String customType() default "";

	/**
	 * 描述当前字段是否可插入
	 */
	boolean insert() default true;

	/**
	 * 描述当前字段是否可更新
	 */
	boolean update() default true;

}
