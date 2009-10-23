package org.nutz.dao.impl;

import org.nutz.dao.Pager;
import org.nutz.dao.entity.Entity;

public abstract class SpecialPager extends Pager {

	public String getResultSetName(Entity<?> entity) {
		return entity.getViewName();
	}

	public boolean isDefault() {
		return false;
	}

}
