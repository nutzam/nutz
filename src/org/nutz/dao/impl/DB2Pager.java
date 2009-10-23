package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;

public class DB2Pager extends SpecialPager {

	@Override
	public String getSqlPattern(Entity entity) {
		/*
		 * SELECT FROM @tableName WHERE (@fieldName IN (SELECT @fieldName FROM
		 * (SELECT @fieldName, rownumber() OVER(ORDER BY @fieldName ASC) AS rn
		 * FROM @tableName) AS R WHERE R.rn BETWEEN
		 * 
		 * @startNumber AND
		 * 
		 * @endNumber))
		 */
		String fieldName = entity.getIdentifiedField().getColumnName();
		String tableName = entity.getViewName();
		int startIndex = this.getPageSize() * (this.getPageNumber() - 1);
		int endIndex = startIndex + this.getPageSize();
		String ptn = "%%s WHERE (%s IN (SELECT %s FROM (SELECT %s,rownumber() OVER(ORDER BY %s ASC) AS rn FROM %s) AS R "
				+ "WHERE R.rn BETWEEN %d AND %d))";

		return String.format(ptn, fieldName, fieldName, fieldName, fieldName, tableName, startIndex, endIndex);
	}

}
