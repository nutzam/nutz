package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.tools.Tables;

public class AutoGenerateValueTest extends DaoCase {

	@Override
	protected void before() {
		Tables.define(dao, Tables.loadFrom("org/nutz/dao/test/normal/killer.dod"));
	}

	@Override
	protected void after() {}

	@Test
	public void test_var_in_prev() {
		// TODO Oracle 那个 @Prev 有问题，暂时先忽略
		if (pojos.dao().meta().isOracle())
			return;
		pojos.initPet();
		Pet2 pet = new Pet2();
		pet.setName("xb").setAge(10);

		dao.insert(pet);

		assertEquals("dog.xb", pet.getNickName());
	}

	@Test
	public void test_simple() {
		Resident xh = new Resident("XH");
		Resident xw = new Resident("XW");
		dao.insert(xh);
		dao.insert(xw);

		Killer zzh = new Killer("zzh");
		zzh.kill(xh);
		zzh.kill(xw);
		dao.insertRelation(zzh, "killeds");
		dao.insert(zzh);

		zzh = dao.fetch(Killer.class, zzh.getId());
		assertEquals(2, zzh.getKilledCount());
		assertEquals("XW", zzh.getLastKillName());

		Resident gfw = new Resident("GFW");
		Resident bs = new Resident("BS");
		dao.insert(gfw);
		dao.insert(bs);

		Killer cnm = new Killer("CNM");
		cnm.kill(gfw);
		cnm.kill(bs);
		dao.insertRelation(cnm, "killeds");
		dao.insert(cnm);

		cnm = dao.fetch(Killer.class, cnm.getId());
		assertEquals(2, cnm.getKilledCount());
		assertEquals("GFW", cnm.getLastKillName());
	}

	@Test
	public void test_insert_prev_by_fastInsert() {
		Resident xh = new Resident("XH");
		Resident xw = new Resident("XW");
		dao.insert(xh);
		dao.insert(xw);

		Killer zzh = new Killer("zzh");
		zzh.kill(xh);
		zzh.kill(xw);
		dao.insertRelation(zzh, "killeds");
		dao.fastInsert(zzh);

		zzh = dao.fetch(Killer.class, "zzh");
		assertEquals(2, zzh.getKilledCount());
		assertEquals("XW", zzh.getLastKillName());
	}

}
