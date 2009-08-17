package org.nutz.ioc;

import org.nutz.dao.test.meta.Base;

public class BaseDeposer implements ObjCallback<Base> {

	@Override
	public void invoke(Base base) {
		base.setName("!!!");
	}

}
