package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;

/**
 * 这个接口描述了一个完整的分页信息。各个实现类，需根据不同的数据库具体情况， 实现这些接口函数。
 * <p>
 * 比较轻松的办法是从 'org.nutz.dao.pager.AbstractPager' 继承，因为它已经实现了除了 format 之外的所有 的方法。
 * <p>
 * 如果你想将你的 Pager 对象整合到 NutDao 实现中，请重新实现 PagerMaker 接口，从 DefaultPagerMaker 继承即可。
 * <p>
 * 通常，你可以通过 Dao 接口的 createPager() 方法，创建你需要的 Pager 实例。它会自动适配数据库类型
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.Dao
 * 
 */
public interface Pager {

	/**
	 * 一共有多少页
	 */
	int getPageCount();

	/**
	 * 当前是第几页， 从 1 开始
	 */
	int getPageNumber();

	/**
	 * 设置页码
	 */
	Pager setPageNumber(int pageNumber);

	/**
	 * 一页可以有多少条记录
	 */
	int getPageSize();

	/**
	 * 设置一页可以有多少条记录
	 */
	Pager setPageSize(int pageSize);

	/**
	 * 整个查询，一共有多少条记录
	 */
	int getRecordCount();

	/**
	 * 设置整个查询一共有多少条记录
	 */
	Pager setRecordCount(int recordCount);

	/**
	 * 当前页之前，还应该有多少条记录
	 */
	int getOffset();

	/**
	 * 根据这以下三个参数，生成查询的 SQL
	 * 
	 * @param entity
	 *            实体对象。你的 Pager 将对这个实体生成查询的 SQL。通过 entity.getViewName()
	 *            你可以获得要查询实体的表|视图名称
	 * @param fields
	 *            字段名 -- <i>已经用逗号替你分隔号了</i>
	 * @param cnd
	 *            条件 SQL -- <i>包括 WHERE 或 ORDER BY 关键字，可能前后有空白
	 * @return 当前数据库，分页查询语句
	 */
	String toSql(Entity<?> entity, String fields, String cnd);

	/**
	 * 你希望查询的时候，建立什么类型的 ResultSet，可以允许的值为：
	 * <ul>
	 * <li>ResultSet.TYPE_FORWARD_ONLY
	 * <li>ResultSet.TYPE_SCROLL_INSENSITIVE
	 * <li>ResultSet.TYPE_SCROLL_SENSITIVE
	 * </ul>
	 * 一般的说，你为特殊的数据库建立的 Pager，都要返回 ResultSet.TYPE_FORWARD_ONLY
	 * 
	 * @see java.sql.ResultSet
	 */
	int getResultSetType();

}
