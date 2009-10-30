package org.nutz.dao.pager;


/**
 * 这个接口描述了一个完整的分页信息。各个实现类，需根据不同的数据库具体情况， 实现这些接口函数。
 * <p>
 * 比较轻松的办法是从 'org.nutz.dao.pager.AbstractPager' 继承，因为它已经实现了除了 format 之外的所有 的方法。
 * <p>
 * 如果你想将你的 Pager 对象整合到 NutDao 实现中，请重新实现 PagerMaker 接口，从 DefaultPagerMaker 继承即可。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public interface Pager {

	/**
	 * 一共有多少页
	 */
	int getPageCount();

	/**
	 * 当前是第几页
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
	 * 将传入的 Sql 对象格式化成新的 Sql 对象，新的 Sql 對象支持 Sql 本地方言进行分页
	 * <p>
	 * 如果，你返回 Null，则表示当前数据库不支持分页，那么 Nutz.Dao 会采用 JDBC 移动游标
	 * 的方式来替你分页。这个比较低效。但是基本可以正常工作
	 */
	String format(String sql);

}
