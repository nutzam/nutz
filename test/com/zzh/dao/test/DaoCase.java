package com.zzh.dao.test;

import org.junit.After;
import org.junit.Before;

import com.zzh.Main;
import com.zzh.dao.Dao;
import com.zzh.dao.test.meta.Pojos;
import com.zzh.ioc.Nut;

public abstract class DaoCase {

	protected Dao dao;
	protected Nut nut;
	protected Pojos pojos;

	@Before
	public void setUp() {
		nut = Main.getNut("com/zzh/dao/test/meta/pojo.js");
		dao = nut.get(Dao.class, "dao");
		pojos = nut.get(Pojos.class, "metas");
		before();
	}

	@After
	public void tearDown() {
		after();
	}

	protected void before() {}

	protected void after() {}

}
