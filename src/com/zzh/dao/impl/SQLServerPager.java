package com.zzh.dao.impl;

import com.zzh.dao.Pager;
import com.zzh.dao.entity.Entity;

public class SQLServerPager extends Pager {

	@Override
	protected String getLimitString(Entity<?> entity) {
		// TODO implement it
		return null;
	}

}
