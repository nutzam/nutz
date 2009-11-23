package org.nutz.dao.tools.impl;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;

/**
 * 封装了数据库之间的不同
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface SqlExpert {

	/**
	 * @param pkNum
	 *            主键数量
	 * @param df
	 *            数据库字段定义
	 * @return 在自己数据库中，对应的字段定义
	 */
	String tellField(int pkNum, DField df);

	/**
	 * @param dt
	 *            数据表定义
	 * @return 数据库定义主键的语句
	 */
	String tellPKs(DTable dt);

	/**
	 * @return 一个字符串模板，包括占位符：
	 *         <ul>
	 *         <li>${table} 表名
	 *         <li>${fields} 字段
	 *         <li>${pks} 主键约束
	 *         </ul>
	 */
	String tellCreateSqlPattern();

	/**
	 * @param dt
	 *            数据表定义
	 * @param createTable
	 *            建表 SQL
	 * @return 可执行的 SQL，不同的数据库，肯能还需要组合其他的 SQL
	 */
	Sql evalCreateSql(DTable dt, Sql createTable);

	/**
	 * @param dt
	 *            数据表定义
	 * @param dropTable
	 *            删表 SQL
	 * @return 可执行的 SQL，不同的数据库，肯能还需要组合其他的 SQL
	 */
	Sql evalDropSql(DTable dt, Sql dropTable);
}
