package org.nutz.dao.pager;

import static java.lang.String.format;

import java.sql.ResultSet;

import org.nutz.dao.entity.Entity;

public class UnknownPager extends AbstractPager {

	@Override
	public int getResultSetType() {
		return ResultSet.TYPE_SCROLL_INSENSITIVE;
	}

	public String toSql(Entity<?> entity, String fields, String cnd) {
		return format("SELECT %s FROM %s %s", fields, entity.getViewName(), cnd);
	}

}
