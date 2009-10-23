package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;

public class MySQLPager extends SpecialPager {

	@Override
	public String getSqlPattern(Entity entity) {
		return String.format("%%s LIMIT %d,%d", this.getPageSize(),this.getOffset());
	}

}
