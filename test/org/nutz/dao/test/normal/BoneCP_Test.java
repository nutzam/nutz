package org.nutz.dao.test.normal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nutz.dao.DaoException;
import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.Tank;
import org.nutz.dao.tools.Tables;
import org.nutz.trans.Atom;


public class BoneCP_Test extends DaoCase {
	
	private Platoon platoon;

	@Override
	protected void before() {
		pojos.init();
		platoon = pojos.create4Platoon(Base.make("blue"), "seals");
	}
	
	@Test
	public void clear_links() {
		TableName.run(platoon, new Atom() {
			public void run() {}
		});
		Tables.define(dao, Tables.loadFrom("org/nutz/dao/test/meta/pet.dod"));
		Sql sql1 = Sqls.create("INSERT INTO t_pet (name) VALUES ('A')");
		Sql sql2 = Sqls.create("INSERT INTO t_pet (nocol) VALUES ('B')");
		try {
			dao.execute(sql1, sql2);
			fail();
		}
		catch (DaoException e) {}
		assertEquals(0, dao.count(Pet.class));
	}
}
