package org.nutz.ioc;

import org.nutz.dao.test.meta.Base;

public class BaseDeposer implements ObjCallback<Base> {

	public void invoke(Base base) {
		base.setName("!!!");
	}

}
