package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;

public class SQLServerPager extends SpecialPager {

	@Override
	public String getSqlPattern(Entity entity) {
		return "%s";
	}

	@Override
	public String getResultSetName(Entity entity) {
		/*
		 * SELECT TOP @pageSize FROM @tableName WHERE
		 * @fieldName NOT IN (SELECT TOP
		 * @pageSize(@page-1) @fieldName FROM @tableName
		 * ORDER BY @fieldName ASC ) ORDER BY @fieldName
		 * ASC
		 */
		EntityField field = entity.getIdentifiedField();
		String viewName = entity.getViewName();
		String fieldName = field.getField().getName();
		int pageSize = this.getPageSize();
		int offSet = pageSize * (this.getPageNumber() - 1);

		String querySQL = "(SELECT TOP %d * FROM %s WHERE %s NOT IN (SELECT TOP %d %s FROM %s ORDER BY %s ASC) ORDER BY %s ASC) AS R";

		return String.format(querySQL, pageSize, viewName, fieldName, offSet, fieldName, viewName,
				fieldName, fieldName);
	}

}
