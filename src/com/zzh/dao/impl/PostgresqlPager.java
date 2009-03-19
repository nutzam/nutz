package com.zzh.dao.impl;

import com.zzh.dao.Pager;

public class PostgresqlPager extends Pager {

	@Override
	protected String getLimitString() {
		return String.format("%%s LIMIT %d OFFSET %d", this.getPageSize(),this.getOffset());
	}

}
