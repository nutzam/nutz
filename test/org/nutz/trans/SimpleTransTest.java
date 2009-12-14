package org.nutz.trans;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.tools.Tables;
import org.nutz.lang.Lang;

public class SimpleTransTest extends DaoCase {

	@Before
	public void before() {
		Tables.run(dao, Tables.define("org/nutz/trans/trans.dod"));
		dao.insert(Cat.create("xb"));

	}

	@Test
	public void test_update_rollback() {

		final Cat cat = dao.fetch(Cat.class, "xb");
		try {
			Trans.exec(new Atom() {
				public void run() {
					cat.setName("PPP");
					dao.update(cat);
					throw Lang.makeThrow("Quite!!!");
				}
			});
			fail();
		} catch (Exception e) {}
		Cat xb = dao.fetch(Cat.class, "xb");
		assertTrue(xb.getId() > 0);
	}

	@Test
	public void test_batch_update_rollback() {
		final Cat cat1 = dao.fetch(Cat.class, "xb");
		final Cat cat2 = new Cat();
		cat2.setId(cat1.getId() + 1);
		cat2.setMaster(cat1.getMaster());
		cat2.setMasterId(cat1.getMasterId());
		cat2.setName("xb2");
		dao.insert(cat2);
		assertTrue(dao.fetch(Cat.class, "xb2").getName().equals("xb2"));
		try {
			Trans.exec(new Atom() {
				public void run() {
					cat1.setName("p1");
					dao.update(cat1);
					cat2.setName("p2");
					dao.update(cat2);
					throw Lang.makeThrow("Quite!!!");
				}
			});
		} catch (Exception e) {}
		assertTrue(dao.fetch(Cat.class, "xb").getName().equals("xb"));
		assertTrue(dao.fetch(Cat.class, "xb2").getName().equals("xb2"));
		dao.delete(cat2);
	}

}
