package org.nutz.trans;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.Lang;

public class SimpleTransTest extends DaoCase {

	@Before
	public void before() {
		Sqls.executeDefinitionFile(dao, "org/nutz/trans/trans.dod");
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

}
