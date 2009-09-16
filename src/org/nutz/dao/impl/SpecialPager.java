package org.nutz.dao.impl;

import org.nutz.dao.Pager;

public abstract class SpecialPager extends Pager {

	@Override
	public boolean isDefault() {
		return false;
	}

}
