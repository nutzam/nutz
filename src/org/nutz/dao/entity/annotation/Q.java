package org.nutz.dao.entity.annotation;

import org.nutz.dao.DatabaseType;

/**
 * 为 '@Next' 注解声明的可执行 SQL
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public @interface Q {

	/**
	 * 本条 SQL 可以应用到的数据库。DatabaseType.UNKNOWN 为默认值，表示 适用于任何数据库。
	 * 
	 * @see org.nutz.dao.DatabaseType
	 */
	DatabaseType db() default DatabaseType.UNKNOWN;

	/**
	 * SQL 语句，支持动态占位符。
	 * <ul>
	 * <li>变量: $XXX ，由 TableName 来设置，以支持动态表名
	 * <li>参数: @XXX， 直接参考对象自身的属性值
	 * </ul>
	 * 
	 * @see org.nutz.dao.TableName
	 */
	String value();

	/**
	 * 本条 SQL 是在执行对象插入前执行，还是在对象插入的 数据库之后执行
	 * <p>
	 * 如果你的数据库支持子查询，将其设为 false，效率较高。
	 */
	boolean before() default true;

}
