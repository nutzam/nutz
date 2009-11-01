package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;

public class PostgresqlPager extends AbstractPager {

	public String format(String sql) {
		StringBuilder sb = new StringBuilder(sql);
		sb.append(" LIMIT ").append(getPageSize()).append(" OFFSET ").append(getOffset());
		return sb.toString();
	}

	public String toSql(Entity<?> entity, String fields, String cnd) {
		return String.format("SELECT %s FROM %s %s LIMIT %d  OFFSET  %d", fields, entity
				.getViewName(), cnd, getPageSize(), getOffset());
	}

}
