package com.zzh.dao.test;

import org.junit.After;
import org.junit.Before;

import com.zzh.Main;
import com.zzh.dao.Dao;
import com.zzh.dao.test.meta.Pojos;
import com.zzh.ioc.Ioc;

public abstract class DaoCase {

	protected Dao dao;
	protected Ioc ioc;
	protected Pojos pojos;

	@Before
	public void setUp() {
		ioc = Main.getIoc("com/zzh/dao/test/meta/pojo.js");
		dao = ioc.get(Dao.class, "dao");
		pojos = ioc.get(Pojos.class, "metas");
		before();
	}

	@After
	public void tearDown() {
		after();
	}

	protected void before() {}

	protected void after() {}

}
