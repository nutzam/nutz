package com.zzh.ioc;

import com.zzh.dao.test.meta.Base;

public class BaseDeposer implements Deposer<Base> {

	@Override
	public void depose(Base base) {
		base.setName("!!!");
	}

}
