package com.zzh.ioc;

import com.zzh.dao.test.meta.Country;

public class CountryDeposer implements Deposer<Country> {

	@Override
	public void depose(Country c) {
		c.setName("#");
	}

}
