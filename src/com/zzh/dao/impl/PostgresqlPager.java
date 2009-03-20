package com.zzh.dao.impl;

import com.zzh.dao.Pager;
import com.zzh.dao.entity.Entity;

public class PostgresqlPager extends Pager {

	@Override
	protected String getLimitString(Entity<?> entity) {
		return String.format("%%s LIMIT %d OFFSET %d", this.getPageSize(),this.getOffset());
	}

}
