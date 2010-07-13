package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;

/**
 * 今天突然需要将一个nutz的小程序移植到db2上，结果发现DB2Pager竟然没有实现？
 * <p>
 * 可能是db2用的人少吧，于是自己实现了一下，如下
 * 
 * @author argszero(argszero@gmail.com)
 */
public class DB2Pager extends AbstractPager {

	private static String PTN = "SELECT * FROM ("
								+ "SELECT ROW_NUMBER() OVER() AS ROWNUM ,T.* FROM ("
								+ "SELECT %s FROM %s %s"
								+ ") T "
								+ ") AS A WHERE ROWNUM BETWEEN %d AND %d";

	public String toSql(Entity<?> entity, String fields, String cnd) {
		int from = this.getOffset();
		int to = from + this.getPageSize();
		return String.format(PTN, fields, entity.getViewName(), cnd, from, to);
	}

}
