package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;

public class MysqlPager extends AbstractPager {

	public String toSql(Entity<?> entity, String table, String fields, String cnd) {
		return String.format("SELECT %s FROM %s %s LIMIT %d, %d", fields, table, cnd,
				getOffset(), getPageSize());
	}
}
