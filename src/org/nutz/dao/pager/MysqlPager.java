package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;

public class MysqlPager extends AbstractPager {

	public String toSql(Entity<?> entity, String fields, String cnd) {
		return String.format("SELECT %s FROM %s %s LIMIT %d, %d", fields, entity.getViewName(),
				cnd, getOffset(), getPageSize());
	}
}
