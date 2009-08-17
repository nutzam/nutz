package org.nutz.ioc;

import org.nutz.dao.test.meta.Country;

public class CountryDeposer implements ObjCallback<Country> {

	@Override
	public void invoke(Country c) {
		c.setName("#");
	}

}
