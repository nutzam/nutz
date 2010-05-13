package org.nutz.dao.pager;

import org.nutz.dao.DatabaseMeta;

/**
 * 生成 Pager 的逻辑
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface PagerMaker {

	/**
	 * 返回 Pager 实例，如果 pageNumber 小于 1 或者 pageSize 小于等于 0 ，那么表示不分页 则返回 null
	 * 
	 * @param meta
	 *            数据库类型
	 * @param pageNumber
	 *            页码，从1 开始
	 * @param pageSize
	 *            页大小
	 * @return Pager 实例
	 */
	Pager make(DatabaseMeta meta, int pageNumber, int pageSize);

}
