package com.zzh.dao.impl;

import com.zzh.dao.Pager;

public class MySQLPager extends Pager {

	@Override
	protected String getLimitString() {
		return String.format("%%s LIMIT %d,%d", this.getPageSize(),this.getOffset());
	}

}
